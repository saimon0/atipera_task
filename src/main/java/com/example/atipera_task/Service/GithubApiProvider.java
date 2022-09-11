package com.example.atipera_task.Service;
import com.example.atipera_task.Exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;
import static org.apache.tomcat.util.http.fileupload.FileUploadBase.CONTENT_TYPE;

@Service
public class GithubApiProvider {
    final String apiUrl = "https://api.github.com/";
    final WebClient webClient;

    GithubApiProvider() {
        this.webClient = WebClient
                .builder()
                .build();
    }

    public String getRepoBranches(String repoName, String ownerName) {
        return webClient
                .get()
                .uri(apiUrl + "/repos/" + ownerName + "/" + repoName + "/branches")
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() != HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        response -> Mono.error(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error")))
                .bodyToMono(String.class).block();
    }

    public String getUserRepos(String user) {

        return webClient
                .get()
                .uri(apiUrl + "users/" + user + "/repos")
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        response -> Mono.error(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error")))
                .bodyToMono(String.class).block();
    }

    public boolean userExists(String userName) throws ApiException {
            webClient
                    .get()
                    .uri(apiUrl + "users/" + userName)
                    .retrieve()
                    .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(),
                            response -> Mono.error(new ApiException(HttpStatus.NOT_FOUND.value(), "User was not found")))
                    .onStatus(status -> status.value() == HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            response -> Mono.error(new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error")))
                    .bodyToMono(String.class).block();
        return true;
    }
}
