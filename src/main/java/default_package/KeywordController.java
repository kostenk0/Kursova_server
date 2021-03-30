package default_package;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KeywordController {
    private static final String template = "%s";

    @RequestMapping("/keyword")
    public String keyword(@RequestParam(value = "value") String keyword){
        return new Keyword(String.format(template, keyword)).getKeyword();
    }
}
