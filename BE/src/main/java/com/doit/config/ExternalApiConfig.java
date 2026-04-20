package com.doit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for External APIs to get real IELTS data
 * 
 * FREE APIs that can be used:
 * 
 * 1. Dictionary API (FREE - No key required)
 *    - URL: https://api.dictionaryapi.dev/api/v2/entries/en/{word}
 *    - Features: Definitions, phonetics, audio pronunciation, examples
 *    - Use for: Vocabulary learning, word definitions
 * 
 * 2. Words API (Freemium - 2500 requests/day free)
 *    - URL: https://wordsapiv1.p.rapidapi.com/words/{word}
 *    - Features: Definitions, synonyms, antonyms, examples, syllables
 *    - Use for: Academic vocabulary, thesaurus
 * 
 * 3. Oxford Dictionaries API (Paid but reliable)
 *    - URL: https://od-api.oxforddictionaries.com/api/v2
 *    - Features: Official definitions, pronunciations
 * 
 * 4. LibreTranslate (FREE - Self-hosted option)
 *    - URL: https://libretranslate.com/
 *    - Features: Translation between languages
 * 
 * 5. Text-to-Speech APIs for Listening:
 *    - Google Cloud TTS (paid)
 *    - Amazon Polly (paid)
 *    - ResponsiveVoice (freemium)
 * 
 * For IELTS Practice Content, sources include:
 * - Cambridge IELTS Books (licensed content)
 * - British Council free samples
 * - IELTS.org official samples
 */
@Configuration
public class ExternalApiConfig {

    // Dictionary API - FREE, no key required
    public static final String DICTIONARY_API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    // Words API via RapidAPI - 2500 free requests/day
    @Value("${external.api.rapidapi.key:}")
    private String rapidApiKey;
    
    public static final String WORDS_API_URL = "https://wordsapiv1.p.rapidapi.com/words/";
    public static final String WORDS_API_HOST = "wordsapiv1.p.rapidapi.com";

    // Text-to-Speech for Listening Practice
    @Value("${external.api.google.tts.key:}")
    private String googleTtsKey;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getRapidApiKey() {
        return rapidApiKey;
    }

    public String getGoogleTtsKey() {
        return googleTtsKey;
    }
}
