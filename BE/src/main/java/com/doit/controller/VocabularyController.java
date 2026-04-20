package com.doit.controller;

import com.doit.dto.common.ApiResponse;
import com.doit.service.VocabularyApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for vocabulary/dictionary features using real API data
 */
@RestController
@RequestMapping("/api/v1/vocabulary")
@RequiredArgsConstructor
public class VocabularyController {

    private final VocabularyApiService vocabularyApiService;

    /**
     * Get definition of a word from Dictionary API
     * 
     * @param word The word to look up
     * @return Word definition with phonetics and examples
     */
    @GetMapping("/definition/{word}")
    public ResponseEntity<ApiResponse<Object>> getDefinition(@PathVariable String word) {
        Object definition = vocabularyApiService.getWordDefinition(word);
        if (definition != null) {
            return ResponseEntity.ok(ApiResponse.success("Definition found", definition));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get detailed information about a word
     * 
     * @param word The word to look up
     * @return Detailed word info including definitions, synonyms, antonyms
     */
    @GetMapping("/details/{word}")
    public ResponseEntity<ApiResponse<Object>> getWordDetails(@PathVariable String word) {
        Object details = vocabularyApiService.getWordDetails(word);
        if (details != null) {
            return ResponseEntity.ok(ApiResponse.success("Word details found", details));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get pronunciation audio URL for a word
     * 
     * @param word The word to get pronunciation for
     * @return Audio URL
     */
    @GetMapping("/pronunciation/{word}")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPronunciation(@PathVariable String word) {
        String audioUrl = vocabularyApiService.getPronunciationUrl(word);
        Map<String, String> result = new HashMap<>();
        result.put("word", word);
        result.put("audioUrl", audioUrl);
        
        if (audioUrl != null) {
            return ResponseEntity.ok(ApiResponse.success("Pronunciation found", result));
        }
        return ResponseEntity.ok(ApiResponse.success("No audio available", result));
    }

    /**
     * Get synonyms for a word
     * 
     * @param word The word to find synonyms for
     * @return List of synonyms
     */
    @GetMapping("/synonyms/{word}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSynonyms(@PathVariable String word) {
        List<String> synonyms = vocabularyApiService.getSynonyms(word);
        Map<String, Object> result = new HashMap<>();
        result.put("word", word);
        result.put("synonyms", synonyms);
        result.put("count", synonyms.size());
        
        return ResponseEntity.ok(ApiResponse.success("Synonyms retrieved", result));
    }

    /**
     * Get antonyms for a word
     * 
     * @param word The word to find antonyms for
     * @return List of antonyms
     */
    @GetMapping("/antonyms/{word}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAntonyms(@PathVariable String word) {
        List<String> antonyms = vocabularyApiService.getAntonyms(word);
        Map<String, Object> result = new HashMap<>();
        result.put("word", word);
        result.put("antonyms", antonyms);
        result.put("count", antonyms.size());
        
        return ResponseEntity.ok(ApiResponse.success("Antonyms retrieved", result));
    }
}
