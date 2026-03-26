package com.github.curriculeon.controller;

import com.github.curriculeon.model.Poll;
import com.github.curriculeon.model.PollOption;
import com.github.curriculeon.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.*;

@Controller
public class PollController {
    private final PollService pollService;

    @Autowired
    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        List<Poll> recent = pollService.recentPolls();
        model.addAttribute("polls", recent);
        model.addAttribute("now", Instant.now());
        return "index";
    }

    @GetMapping("/create")
    public String createForm() {
        return "create";
    }

    @PostMapping("/create")
    public String createSubmit(@RequestParam String question, @RequestParam(name = "option") List<String> options) {
        pollService.createPoll(question, options);
        return "redirect:/";
    }

    @GetMapping("/poll/{id}")
    public String viewPoll(@PathVariable Long id, Model model, HttpServletRequest request) {
        Poll poll = pollService.findById(id).orElseThrow(() -> new NoSuchElementException("Poll not found"));
        boolean visible = pollService.isResultsVisible(poll);
        model.addAttribute("poll", poll);
        model.addAttribute("resultsVisible", visible);
        if (visible) {
            Map<Long, Long> counts = pollService.countsForPoll(poll);
            long total = counts.values().stream().mapToLong(Long::longValue).sum();
            model.addAttribute("counts", counts);
            model.addAttribute("totalVotes", total);
        }

        // ensure a voter fingerprint cookie exists so frontend will send it on vote
        String fingerprint = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("ANV_VOTER".equals(c.getName())) {
                    fingerprint = c.getValue();
                    break;
                }
            }
        }
        model.addAttribute("fingerprint", fingerprint);
        return "poll";
    }

    @PostMapping("/poll/{id}/vote")
    public String vote(@PathVariable Long id, @RequestParam Long optionId,
                       @CookieValue(value = "ANV_VOTER", required = false) String fingerprint,
                       HttpServletResponse response,
                       RedirectAttributes redirectAttributes) {
        // if no fingerprint cookie, create one and set it for future requests
        if (fingerprint == null || fingerprint.trim().isEmpty()) {
            fingerprint = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("ANV_VOTER", fingerprint);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 365); // 1 year
            response.addCookie(cookie);
        }

        try {
            pollService.vote(id, optionId, fingerprint);
        } catch (IllegalStateException ex) {
            // duplicate vote detected — add flash message so UI can display it
            redirectAttributes.addFlashAttribute("error", "You have already voted on this poll from this browser.");
            return "redirect:/poll/" + id;
        }

        redirectAttributes.addFlashAttribute("message", "Thanks — your anonymous vote was recorded.");
        return "redirect:/poll/" + id;
    }

}
