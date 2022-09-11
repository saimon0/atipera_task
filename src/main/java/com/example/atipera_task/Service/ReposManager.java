package com.example.atipera_task.Service;
import com.example.atipera_task.Exception.ApiException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ReposManager {
    GithubApiProvider githubApiProvider;
    HashMap<String, String> repoNameOwnerMap = new HashMap<>();

    ReposManager(GithubApiProvider githubApiProvider) {
        this.githubApiProvider = githubApiProvider;
    }

    public Map<String, Object> handleGetRequest(String user, Map<String, String> requestHeaders) throws ApiException {
        if (requestHeaders.containsKey("content-type") && Objects.equals(requestHeaders.get("content-type"), "application/json")) {
            if (githubApiProvider.userExists(user)) {
                String response = githubApiProvider.getUserRepos(user);
                repoNameOwnerMap = parseRepoUrls(response);
                return buildUserReposDetails(repoNameOwnerMap).toMap();
            }
        } else {
            throw new ApiException(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "Invalid Content-Type header!");
        }
        throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error");
    }

    private JSONObject buildUserReposDetails(HashMap<String, String> repoNameOwnerMap) {
        String response;
        JSONObject jsonReposDetails = new JSONObject();

        for (Map.Entry<String, String> repoNameOwner : repoNameOwnerMap.entrySet()) {
            String repoName = repoNameOwner.getKey();
            String ownerName = repoNameOwner.getValue();

            response = githubApiProvider.getRepoBranches(repoName, ownerName);
            JSONArray repoBranches = new JSONArray(response);

            JSONObject branches = new JSONObject();
            for(int i = 0; i < repoBranches.length(); i++) {
                JSONObject object = repoBranches.getJSONObject(i);
                branches.put(object.getString("name"), new JSONObject().put("Branch Name", object.getString("name"))
                        .put("Last Commit SHA", object.getJSONObject("commit").get("sha")));
            }
            jsonReposDetails
                    .put(repoName, new JSONObject()
                            .put("Repository Name", repoName)
                            .put("Owner Login", ownerName)
                            .put("Branches", branches)
                    );
        }
        return jsonReposDetails;
    }

    private HashMap<String, String> parseRepoUrls(String response) {
        HashMap<String, String> map = new HashMap<>();
        JSONArray responseJson = new JSONArray(response);
        for(int i = 0; i < responseJson.length(); i++)
        {
            JSONObject object = responseJson.getJSONObject(i);
            if (!object.getBoolean("fork")) {
                map.put(object.getString("name"), object.getJSONObject("owner").getString("login"));
            }
        }
        return map;
    }

}
