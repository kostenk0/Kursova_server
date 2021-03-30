package default_package;

public class SearchResult {
    private String url;
    private String snippet;

    public SearchResult(String url, String snippet) {
        setUrl(url);
        setSnippet(snippet);
    }

    public String getUrl() {
        return url;
    }

    private void setUrl(String url) {
        this.url = url;
    }

    public String getSnippet() {
        return snippet;
    }

    private void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public void showValue(){
        System.out.println(getUrl());
        System.out.println(getSnippet());
        System.out.println("--------------------------------------");
    }
}