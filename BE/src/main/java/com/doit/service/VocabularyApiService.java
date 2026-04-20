package com.doit.service;

import com.doit.config.ExternalApiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Service to fetch real English vocabulary data from external APIs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VocabularyApiService {

    private final RestTemplate restTemplate;
    private final ExternalApiConfig apiConfig;

    /**
     * Get word definition from Free Dictionary API
     * API: https://api.dictionaryapi.dev/api/v2/entries/en/{word}
     * 
     * @param word The word to look up
     * @return JSON response with definition, phonetics, examples
     */
    public Object getWordDefinition(String word) {
        try {
            String url = ExternalApiConfig.DICTIONARY_API_URL + word;
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            log.debug("Dictionary API response for '{}': {}", word, response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching definition for word '{}': {}", word, e.getMessage());
            return null;
        }
    }

    /**
     * Get detailed word information from Words API (RapidAPI)
     * Requires API key - 2500 free requests/day
     * 
     * @param word The word to look up
     * @return JSON response with definitions, synonyms, antonyms, examples
     */
    public Object getWordDetails(String word) {
        String apiKey = apiConfig.getRapidApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("RapidAPI key not configured, using free Dictionary API");
            return getWordDefinition(word);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-RapidAPI-Key", apiKey);
            headers.set("X-RapidAPI-Host", ExternalApiConfig.WORDS_API_HOST);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            String url = ExternalApiConfig.WORDS_API_URL + word;

            ResponseEntity<Object> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Object.class
            );
            log.debug("Words API response for '{}': {}", word, response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching details for word '{}': {}", word, e.getMessage());
            return getWordDefinition(word); // Fallback to free API
        }
    }

    /**
     * Get pronunciation audio URL for a word
     * 
     * @param word The word to get pronunciation for
     * @return Audio URL or null if not found
     */
    public String getPronunciationUrl(String word) {
        try {
            Object definition = getWordDefinition(word);
            if (definition instanceof List) {
                List<?> results = (List<?>) definition;
                if (!results.isEmpty()) {
                    Map<?, ?> firstResult = (Map<?, ?>) results.get(0);
                    List<?> phonetics = (List<?>) firstResult.get("phonetics");
                    if (phonetics != null) {
                        for (Object phonetic : phonetics) {
                            Map<?, ?> phoneticMap = (Map<?, ?>) phonetic;
                            String audio = (String) phoneticMap.get("audio");
                            if (audio != null && !audio.isEmpty()) {
                                // Fix URL if needed (some start with //)
                                if (audio.startsWith("//")) {
                                    return "https:" + audio;
                                }
                                return audio;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting pronunciation for '{}': {}", word, e.getMessage());
        }
        return null;
    }

    /**
     * Get synonyms for a word
     * 
     * @param word The word to find synonyms for
     * @return List of synonyms
     */
    @SuppressWarnings("unchecked")
    public List<String> getSynonyms(String word) {
        Set<String> allSynonyms = new HashSet<>();
        try {
            Object definition = getWordDefinition(word);
            if (definition instanceof List) {
                List<?> results = (List<?>) definition;
                for (Object result : results) {
                    Map<?, ?> resultMap = (Map<?, ?>) result;
                    List<?> meanings = (List<?>) resultMap.get("meanings");
                    if (meanings != null) {
                        for (Object meaning : meanings) {
                            Map<?, ?> meaningMap = (Map<?, ?>) meaning;
                            // Get synonyms at meaning level
                            List<String> meaningSynonyms = (List<String>) meaningMap.get("synonyms");
                            if (meaningSynonyms != null) {
                                allSynonyms.addAll(meaningSynonyms);
                            }
                            // Get synonyms from each definition
                            List<?> definitions = (List<?>) meaningMap.get("definitions");
                            if (definitions != null) {
                                for (Object def : definitions) {
                                    Map<?, ?> defMap = (Map<?, ?>) def;
                                    List<String> defSynonyms = (List<String>) defMap.get("synonyms");
                                    if (defSynonyms != null) {
                                        allSynonyms.addAll(defSynonyms);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting synonyms for '{}': {}", word, e.getMessage());
        }
        return new ArrayList<>(allSynonyms);
    }

    /**
     * Get antonyms for a word
     * 
     * @param word The word to find antonyms for
     * @return List of antonyms
     */
    @SuppressWarnings("unchecked")
    public List<String> getAntonyms(String word) {
        Set<String> allAntonyms = new HashSet<>();
        try {
            Object definition = getWordDefinition(word);
            if (definition instanceof List) {
                List<?> results = (List<?>) definition;
                for (Object result : results) {
                    Map<?, ?> resultMap = (Map<?, ?>) result;
                    List<?> meanings = (List<?>) resultMap.get("meanings");
                    if (meanings != null) {
                        for (Object meaning : meanings) {
                            Map<?, ?> meaningMap = (Map<?, ?>) meaning;
                            // Get antonyms at meaning level
                            List<String> meaningAntonyms = (List<String>) meaningMap.get("antonyms");
                            if (meaningAntonyms != null) {
                                allAntonyms.addAll(meaningAntonyms);
                            }
                            // Get antonyms from each definition
                            List<?> definitions = (List<?>) meaningMap.get("definitions");
                            if (definitions != null) {
                                for (Object def : definitions) {
                                    Map<?, ?> defMap = (Map<?, ?>) def;
                                    List<String> defAntonyms = (List<String>) defMap.get("antonyms");
                                    if (defAntonyms != null) {
                                        allAntonyms.addAll(defAntonyms);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting antonyms for '{}': {}", word, e.getMessage());
        }
        return new ArrayList<>(allAntonyms);
    }
}
