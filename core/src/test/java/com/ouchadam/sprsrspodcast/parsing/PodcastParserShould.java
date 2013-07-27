package com.ouchadam.sprsrspodcast.parsing;

import com.novoda.sexp.SimpleEasyXmlParser;
import com.novoda.sexp.finder.ElementFinderFactory;
import com.novoda.sexp.parser.ParseFinishWatcher;
import com.ouchadam.sprsrspodcast.XMLHelper;
import com.ouchadam.sprsrspodcast.domain.channel.Channel;
import com.ouchadam.sprsrspodcast.domain.channel.Image;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

public class PodcastParserShould {

    static final XMLHelper.XML CNET_SMALL = XMLHelper.get(XMLHelper.XmlResource.CNET_SMALL);

    XmlParser<Channel> podcastParser;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ElementFinderFactory finderFactory = SimpleEasyXmlParser.getElementFinderFactory();
        InstigatorResult <Channel> instigator = new PodcastIntigator(finderFactory.getTypeFinder(new ChannelParser(finderFactory)), mock(ParseFinishWatcher.class));
        podcastParser = new PodcastParser(instigator);
    }

    @Test
    public void parse_the_correct_channel_title() throws Exception {
        podcastParser.parse(CNET_SMALL.toInputStream());

        assertThat(podcastParser.getResult().getTitle()).isEqualTo("CNET UK Podcast");
    }

    @Test
    public void parse_the_correct_channel_category() throws Exception {
        podcastParser.parse(CNET_SMALL.toInputStream());

        assertThat(podcastParser.getResult().getCategory()).isEqualTo("Technology");
    }

    @Test
    public void parse_the_correct_channel_image() throws Exception {
        podcastParser.parse(CNET_SMALL.toInputStream());

        Image image = podcastParser.getResult().getImage();
        assertThat(image.getUrl()).isEqualTo("http://www.cnet.co.uk/images/rss/logo-cnet.jpg");
        assertThat(image.getLink()).isEqualTo("http://crave.cnet.co.uk/podcast/");
        assertThat(image.getTitle()).isEqualTo("CNET UK Podcast");
        assertThat(image.getWidth()).isEqualTo(88);
        assertThat(image.getHeight()).isEqualTo(56);
    }

    @Test
    public void parse_the_correct_channel_itunes_summary() throws Exception {
        podcastParser.parse(CNET_SMALL.toInputStream());

        assertThat(podcastParser.getResult().getSummary()).isEqualTo("\n" +
                "      Britain's best technology podcast is beamed to your auditory sensors every Friday afternoon direct from CNET UK.\n" +
                "      The honey-toned team give you everything you need to know about the week's hottest tech news, the most\n" +
                "      Crave-worthy new gadgets and answer your best questions and funniest comments. There's a special feature every\n" +
                "      week, in which we tackle a burning tech topic in more detail, and lots more fun besides. Subscribe now, and let us\n" +
                "      know what you think!\n" +
                "    ");
    }

    @Test
    public void parse_the_correct_amount_of_feed_items() throws Exception {
        podcastParser.parse(CNET_SMALL.toInputStream());

        assertThat(podcastParser.getResult().itemCount()).isEqualTo(5);
    }

}