package com.github.curriculeon.integration;

import com.github.curriculeon.model.Poll;
import com.github.curriculeon.model.PollOption;
import com.github.curriculeon.service.PollService;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HtmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PollAjaxIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PollService pollService;

    private Poll poll;
    private WebClient webClient;

    @Before
    public void setup() {
        // create a poll with two options
        poll = pollService.createPoll("Integration test poll", java.util.Arrays.asList("A", "B"));
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
    }

    @After
    public void teardown() {
        if (webClient != null) webClient.close();
    }

    @Test
    public void vote_via_ajax_updates_counts_and_shows_results() throws Exception {
        String url = "http://localhost:" + port + "/poll/" + poll.getId();
        HtmlPage page = webClient.getPage(url);
        // allow initial scripts
        webClient.waitForBackgroundJavaScript(1000);

        // find the first option radio and select it
        // inputs are named 'optionId'
        final com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput radio =
                page.getFirstByXPath("//input[@name='optionId']");
        Assert.assertNotNull("expected radio inputs to exist", radio);
        radio.setChecked(true);

        // click the vote button (the JS intercepts the form submit)
        final com.gargoylesoftware.htmlunit.html.HtmlButton submit =
                page.getFirstByXPath("//form[@id='vote-form']//button");
        Assert.assertNotNull("submit button present", submit);

        HtmlPage after = submit.click();
        // wait for JS (fetch + counts update)
        webClient.waitForBackgroundJavaScript(3000);

        // After the AJAX flow the counts element for the selected option should be present and >= 1
        PollOption opt = poll.getOptions().get(0);
        String countId = "count-" + opt.getId();
        final com.gargoylesoftware.htmlunit.html.HtmlElement countEl = after.getElementById(countId);
        Assert.assertNotNull("count element should exist after vote", countEl);
        String text = countEl.asText();
        int cnt = Integer.parseInt(text.trim());
        Assert.assertTrue("vote count should be at least 1", cnt >= 1);
    }
}
