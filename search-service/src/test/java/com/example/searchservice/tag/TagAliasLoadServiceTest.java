package com.example.searchservice.tag;

import com.example.searchservice.tag.service.TagAliasLoadService;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TagAliasLoadServiceTest {

    @Test
    void getAliases() {
        // given
        TagAliasLoadService service = new TagAliasLoadService();

        // when
        List<String> javaAliases = service.getTagAlias("Java");
        List<String> jsAliases = service.getTagAlias("JavaScript");
        List<String> springAliases = service.getTagAlias("Spring");
        List<String> springBootAliases = service.getTagAlias("Spring Boot");
        List<String> reactAliases = service.getTagAlias("React");
        List<String> htmlAliases = service.getTagAlias("HTML");
        List<String> awsAliases = service.getTagAlias("AWS");

        // then
        System.out.println("Java 별칭 리스트: " + javaAliases);
        System.out.println("JavaScript 별칭 리스트: " + jsAliases);
        System.out.println("Spring 별칭 리스트: " + springAliases);
        System.out.println("Spring Boot 별칭 리스트: " + springBootAliases);
        System.out.println("React 별칭 리스트: " + reactAliases);
        System.out.println("HTML 별칭 리스트: " + htmlAliases);
        System.out.println("AWS 별칭 리스트: " + awsAliases);
    }
}
