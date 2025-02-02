package com.poc.dbutil.config;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CharacterConfig {

    private Map<String, Theme> themes;

    @Setter
    @Getter
    public static class Theme {
        private String name;
        private List<String> characters;
    }

}
