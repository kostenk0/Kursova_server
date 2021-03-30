package default_package;

public class Keyword {
    private final String keyword;

    public Keyword(String keyword){
        this.keyword = keyword;
        Parser parser = new Parser(keyword);
        parser.run();
    }

    public String getKeyword(){
        return keyword;
    }
}
