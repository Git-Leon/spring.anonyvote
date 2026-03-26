package com.github.curriculeon.controller;

import com.github.curriculeon.model.Poll;
import com.github.curriculeon.model.PollOption;
import com.github.curriculeon.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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

        // If results are not globally visible, allow a voter who has just voted to see current counts
        boolean viewerHasVoted = false;
        if (!visible && fingerprint != null && !fingerprint.trim().isEmpty()) {
            viewerHasVoted = pollService.hasVoted(id, fingerprint);
            if (viewerHasVoted) {
                Map<Long, Long> counts = pollService.countsForPoll(poll);
                long total = counts.values().stream().mapToLong(Long::longValue).sum();
                model.addAttribute("counts", counts);
                model.addAttribute("totalVotes", total);
            }
        }
        model.addAttribute("viewerHasVoted", viewerHasVoted);
        return "poll";
    }

    @PostMapping("/poll/{id}/vote")
    public Object vote(@PathVariable Long id, @RequestParam Long optionId,
                       @CookieValue(value = "ANV_VOTER", required = false) String fingerprint,
                       HttpServletResponse response,
                       RedirectAttributes redirectAttributes,
                       HttpServletRequest request) {
        // if no fingerprint cookie, create one and set it for future requests
        if (fingerprint == null || fingerprint.trim().isEmpty()) {
            fingerprint = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("ANV_VOTER", fingerprint);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 365); // 1 year
            response.addCookie(cookie);
        }
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        try {
            pollService.vote(id, optionId, fingerprint);
        } catch (IllegalStateException ex) {
            // duplicate vote detected
            if (isAjax) {
                Map<String, String> body = new HashMap<>();
                body.put("error", "You have already voted on this poll from this browser.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
            } else {
                redirectAttributes.addFlashAttribute("error", "You have already voted on this poll from this browser.");
                return "redirect:/poll/" + id;
            }
        }

        if (isAjax) {
            Map<String, String> body = new HashMap<>();
            body.put("message", "Thanks — your anonymous vote was recorded.");
            return ResponseEntity.ok(body);
        } else {
            redirectAttributes.addFlashAttribute("message", "Thanks — your anonymous vote was recorded.");
            return "redirect:/poll/" + id;
        }
    }

    @GetMapping("/poll/{id}/counts")
    @ResponseBody
    public Map<String, Object> counts(@PathVariable Long id) {
        Poll poll = pollService.findById(id).orElseThrow(() -> new NoSuchElementException("Poll not found"));
        Map<Long, Long> counts = pollService.countsForPoll(poll);
        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        Map<String, Object> resp = new HashMap<>();
        resp.put("counts", counts);
        resp.put("totalVotes", total);
        return resp;
    }

}
