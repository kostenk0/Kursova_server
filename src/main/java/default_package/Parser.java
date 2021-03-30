package default_package;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Parser{

    private static final String BING_SEARCH_URL = "http://www.bing.com/search?q=keyword";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.15 (KHTML, like Gecko) Chrome/24.0.1295.0 Safari/537.15";
    private static String keyword;

    public Parser(String keyword){
        this.keyword = keyword;
    }

    private static String createUrl(String searchUrl, String searchStr, int page) {
        return searchUrl.replaceAll("keyword", URLEncoder.encode(searchStr, StandardCharsets.UTF_8))
                + "&first=" + URLEncoder.encode(String.valueOf(page), StandardCharsets.UTF_8);
    }

    private void parser(String searchStr, int count) throws Exception {
        ArrayList<SearchResult> results = new ArrayList<>();
        String url;
        Document htmlDocument = null;

        for (int i = 10; i <= count; i += 10) {
            url = createUrl(BING_SEARCH_URL, searchStr, i + 1);
            Connection connection = Jsoup.connect(url).timeout(20000).userAgent(USER_AGENT);
            try {
                htmlDocument = connection.get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (connection.response().statusCode() == 200) {
                System.out.println("Success. Visiting: " + url);
            } else {
                System.out.println("Failure. Code: " + connection.response().statusCode() + " "
                        + url);
//                throw new Exception();
            }

            Elements linksOnPage = null;
            if (htmlDocument != null) {
                linksOnPage = selectData(htmlDocument);
            }
            results.addAll(addData(linksOnPage));
            connection.execute();
        }
        if(results.size()>0){
            AllResults allResults = new AllResults(searchStr, results);
            allResults.uploadToBD();}
//        System.out.println(allResults.getKeyword());
//        ArrayList<SearchResult> sr = allResults.getResults();
//        for (int i = 0; i < sr.size(); i++) {
//            System.out.println(i);
//            System.out.println(sr.get(i).toString());
//        }
    }

    private ArrayList<SearchResult> addData(Elements linksOnPage) {
        String url;
        String snippet;
        ArrayList<SearchResult> results = new ArrayList<>();
        for (Element link : linksOnPage) {
            url = link.select("h2").select("a[href]").attr("href");
            snippet = String.valueOf(link.select("p").eachText());
            SearchResult searchResult = new SearchResult(url, snippet);
            results.add(searchResult);
        }
        return results;
    }

    private static Elements selectData(Document htmlDocument) {
        return htmlDocument.select("ol[id=\"b_results\"]").select("li[class=\"b_algo\"]");
    }

    private static void showResults(Elements linksOnPage) {
        for (Element link : linksOnPage) {
            System.out.println(link.select("h2").select("a[href]").attr("href"));
            System.out.println(link.select("p").eachText());
        }
    }

    public void run() {
        try {
            parser(keyword, 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}