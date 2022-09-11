package com.example.atipera_task.Controller;
import com.example.atipera_task.Exception.ApiException;
import com.example.atipera_task.Service.ReposManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class ApiController {
    private final ReposManager reposManager;

    ApiController(ReposManager reposManager) {
        this.reposManager = reposManager;
    }

    @RequestMapping(value = "/api/repos/{user}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getRepos(@PathVariable("user") String user, @RequestHeader Map<String, String> contentType) throws ApiException {
        return reposManager.handleGetRequest(user, contentType);
    }

    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public ResponseEntity<?> handleException(ApiException exception) {
        return ResponseEntity.status(exception.getHttpCode()).body(exception.getExceptionMessage());
    }

}
