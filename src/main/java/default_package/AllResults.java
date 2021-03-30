package default_package;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class AllResults {
    private String keyword;
    ArrayList<SearchResult> results;

    public AllResults(String keyword, ArrayList<SearchResult> results){
        this.keyword = keyword;
        this.results = results;
    }

    public String getKeyword(){
        return this.keyword;
    }

    public ArrayList<SearchResult> getResults(){
        return this.results;
    }

    public synchronized void uploadToBD(){
        String connectionUrl = "jdbc:sqlserver://kursova-robota.database.windows.net:1433;database=Kursova;user=yaroslav@kursova-robota;password={i_am_batman10};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement()){
            String keyword = getKeyword();
            ArrayList<SearchResult> arrayList = getResults();

            int autoIncrementKeywordID = 0;
            String maxKeywordID = "SELECT MAX(UniqueID) FROM Keyword";
            ResultSet rsMaxKeywordID = stmt.executeQuery(maxKeywordID);
            while (rsMaxKeywordID.next()) {
                autoIncrementKeywordID = rsMaxKeywordID.getInt(1);
            }
            autoIncrementKeywordID = autoIncrementKeywordID + 1;

            String ifNotExists = "IF NOT EXISTS ( SELECT * FROM Keyword WHERE Keyword='" + keyword + "')\n" +
                    "BEGIN\n" +
                    "    INSERT INTO Keyword (UniqueID, Keyword) VALUES (" + autoIncrementKeywordID + ",'" + keyword + "')\n" +
                    "END";
            stmt.executeUpdate(ifNotExists);

            int keywordID = 0;
            String selectId = "SELECT * FROM Keyword WHERE Keyword = '" + keyword + "';";
            ResultSet rsKeywordID = stmt.executeQuery(selectId);
            while (rsKeywordID.next()) {
                keywordID = rsKeywordID.getInt(1);
            }
            int autoIncrementAllResults = 0;
            String maxAllResultsID = "SELECT MAX(UniqueID) FROM All_Results";
            ResultSet rsMaxAllResultsID = stmt.executeQuery(maxAllResultsID);
            while (rsMaxAllResultsID.next()) {
                autoIncrementAllResults = rsMaxAllResultsID.getInt(1);
            }
            autoIncrementAllResults = autoIncrementAllResults + 1;

            String insertAllResults = "INSERT INTO All_Results(UniqueID ,dateTime , Keyword_ID)" +
                    "VALUES(" + autoIncrementAllResults + ",SYSUTCDATETIME()," + keywordID + ");";
            stmt.executeUpdate(insertAllResults);
            int autoIncrementSearchResult = 0;
            for (int i = 0; i < arrayList.size(); i++) {
                String maxSearchResultID = "SELECT MAX(UniqueID) FROM Search_Result";
                ResultSet rsMaxSearchResultID = stmt.executeQuery(maxSearchResultID);
                while (rsMaxSearchResultID.next()) {
                    autoIncrementSearchResult = rsMaxSearchResultID.getInt(1);
                }
                autoIncrementSearchResult = autoIncrementSearchResult + 1;
                String snippet = arrayList.get(i).getSnippet().replace("'","''");
                if (snippet.length() > 127) {
                    snippet = snippet.substring(0, 127);
                }
                String insertSearchResult = "INSERT INTO Search_Result(UniqueID, url, snippet,position, All_Results_ID)" +
                        "VALUES(" + autoIncrementSearchResult + ", " +
                        "'" + arrayList.get(i).getUrl() + "'," +
                        " '" + snippet + "'," +
                        " " + i + "," +
                        " " + autoIncrementAllResults + ")";
                stmt.executeUpdate(insertSearchResult);
            }

        }catch (Exception e){
//            e.printStackTrace();
        }
    }
}