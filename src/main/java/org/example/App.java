package org.example;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class App {

//        GitHub Open Issues Tracker
//        Write a program that would take a public github repository and will determine the following
//        i) Total number of open issues
//        ii) Number of open issues that were opened in the last 24 hours
//        iii) Number of open issues that were opened more than 24 hours ago but less than 7 days ago
//        iv) Number of open issues that were opened more than 7 days ago
//        v) What is the average time taken to close an issue
//        vi) What is the last comment on the issue and its details

    public static boolean isWithIn24Hrs(String createdAt) {
        DateTime dateTime = new DateTime(createdAt); // Converting java.util.Date to Joda-Time DateTime.
        DateTime yesterday = DateTime.now().minusDays(1);
        boolean isBeforeYesterday = dateTime.isBefore(yesterday);
        return isBeforeYesterday;
    }

    private static void getGitHubData(String repoName) throws IOException, ParseException {
        try {
            URL obj = new URL("https://api.github.com/repos/" + repoName + "/issues");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "USER_AGENT");
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in;
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONParser parser = new JSONParser();
                JSONArray json = (JSONArray) parser.parse(response.toString());


                int nosOfOpenIssues = 0;
                long noOfComments = 0;
                int noOfOpenIssuesWithin24hours = 0;
                for (int i = 0; i < json.size(); i++) {
                    JSONObject object = (JSONObject) json.get(i);
                    if (object.get("state").equals("open")) nosOfOpenIssues++;
                    noOfComments += (long) object.get("comments");
                    if (object.get("state").equals("open") && isWithIn24Hrs((String) object.get("created_at"))) {
                        noOfOpenIssuesWithin24hours++;
                    }
                }
                //Total number of open issues and all comments
                System.out.println("Total number of open issues: " + nosOfOpenIssues + " and commments available on repo are: " + noOfComments);
                //Number of open issues that were opened in the last 24 hours
                System.out.println("Number of open issues that were opened in the last 24 hours: " + noOfOpenIssuesWithin24hours);
                //Number of open issues that were opened more than 24 hours ago but less than 7 days ago
                //Number of open issues that were opened more than 7 days ago
                //What is the average time taken to close an issue
                //What is the last comment on the issue and its details
            } else {
                System.out.println("Server Error! Please try again after a while.");
            }
        } catch (Exception e) {
            System.out.println("Error Occurred while reading github repository");
            System.out.println(e.getMessage() + e.getCause());
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        String githubRepo = "cli/cli";
        getGitHubData(githubRepo);
    }
}
