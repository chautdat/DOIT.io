package com.doit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Service để lấy dữ liệu IELTS thật từ các nguồn uy tín
 * - Cambridge IELTS Vocabulary
 * - IELTS Official Sample Tests
 * - British Council Resources
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IeltsDataService {

    private final RestTemplate restTemplate;
    private final VocabularyApiService vocabularyApiService;

    // ============== IELTS VOCABULARY BY BAND ==============
    
    /**
     * Cambridge Academic Word List - 570 từ học thuật quan trọng nhất
     * Nguồn: https://www.victoria.ac.nz/lals/resources/academicwordlist
     */
    public static final List<String> ACADEMIC_WORD_LIST_BAND_6_7 = Arrays.asList(
        // Sublist 1 - Most frequent
        "analysis", "approach", "area", "assessment", "assume", "authority", "available",
        "benefit", "concept", "consistent", "constitutional", "context", "contract", "create",
        "data", "definition", "derived", "distribution", "economic", "environment", "established",
        "estimate", "evidence", "export", "factors", "financial", "formula", "function",
        "identified", "income", "indicate", "individual", "interpretation", "involved", "issues",
        "labour", "legal", "legislation", "major", "method", "occur", "percent", "period",
        "policy", "principle", "procedure", "process", "required", "research", "response",
        "role", "section", "sector", "significant", "similar", "source", "specific", "structure",
        "theory", "variables"
    );

    /**
     * Advanced IELTS Vocabulary - Band 7-8
     * Từ vựng nâng cao thường xuất hiện trong bài thi IELTS
     */
    public static final List<String> ADVANCED_VOCAB_BAND_7_8 = Arrays.asList(
        // Academic & Formal
        "advocate", "albeit", "allocate", "ambiguous", "analogous", "anticipate", "arbitrary",
        "attribute", "coincide", "commence", "compatible", "comprehensive", "comprise", "conceive",
        "concurrent", "confer", "confine", "consecutive", "consequent", "constitute", "constrain",
        "construct", "contemporary", "contradict", "contrary", "controversy", "convert", "cooperate",
        "coordinate", "core", "corporate", "correspond", "criteria", "crucial", "deduce", "demonstrate",
        "denote", "deviate", "differentiate", "diminish", "discriminate", "dispose", "distinct",
        "distort", "domain", "dominant", "draft", "duration", "dynamic", "eliminate", "emerge",
        "emphasis", "empirical", "enable", "encounter", "enhance", "enormous", "ensure", "entity",
        "equivalent", "erode", "error", "ethics", "ethnic", "evaluate", "eventual", "evident",
        "evolution", "exceed", "exclude", "exhibit", "expand", "explicit", "exploit", "extract"
    );

    /**
     * IELTS Speaking Topics - Các chủ đề thường gặp trong Speaking Part 1, 2, 3
     * Nguồn: IELTS Simon, British Council
     */
    public static final Map<String, List<String>> SPEAKING_TOPICS = new LinkedHashMap<>() {{
        put("Part 1 - Personal", Arrays.asList(
            "Hometown", "Work/Studies", "Home/Accommodation", "Family", "Friends",
            "Daily Routine", "Food & Cooking", "Weather", "Transport", "Music",
            "Reading", "Movies", "Sports", "Shopping", "Clothes", "Colors",
            "Photography", "Mobile Phones", "Internet", "Social Media"
        ));
        put("Part 2 - Cue Card Topics", Arrays.asList(
            "Describe a person who inspires you",
            "Describe a place you would like to visit",
            "Describe a book that influenced you",
            "Describe a memorable event in your life",
            "Describe a skill you would like to learn",
            "Describe a time when you helped someone",
            "Describe your favorite holiday destination",
            "Describe a historical building in your city",
            "Describe a piece of technology you use daily",
            "Describe a teacher who influenced you"
        ));
        put("Part 3 - Discussion Topics", Arrays.asList(
            "Education system in your country",
            "Impact of technology on society",
            "Environmental issues and solutions",
            "Work-life balance",
            "Globalization effects",
            "Traditional vs modern lifestyles",
            "Health and fitness trends",
            "Media influence on young people",
            "Urban vs rural living",
            "Future of transportation"
        ));
    }};

    /**
     * IELTS Writing Task 2 Topics - Actual exam topics
     * Nguồn: IELTS Liz, IELTS Simon
     */
    public static final Map<String, List<String>> WRITING_TASK2_TOPICS = new LinkedHashMap<>() {{
        put("Education", Arrays.asList(
            "Some people think that the government should provide free university education. To what extent do you agree?",
            "In many countries, children are becoming overweight and unhealthy. Some people think that governments should be responsible. Others think it is the parents' responsibility. Discuss both views.",
            "Some people believe that studying at university or college is the best route to a successful career. Others believe that it is better to get a job straight after school. Discuss both views."
        ));
        put("Technology", Arrays.asList(
            "Many people believe that social media sites have had a negative impact on both individuals and society. To what extent do you agree?",
            "Some people think that robots are very important to human future development. Others think that robots are dangerous and have negative effects. Discuss both views.",
            "With the increasing use of mobile phones and computers, fewer people are writing letters. Some people think that traditional letter writing will disappear completely. To what extent do you agree?"
        ));
        put("Environment", Arrays.asList(
            "Global warming is one of the biggest threats humans face in the 21st century. To what extent do you agree?",
            "Some people think that environmental problems should be solved by the government. Others believe individuals have to take some responsibility. Discuss both views.",
            "Many people believe that we should protect endangered species while others think it is a waste of resources. Discuss both views."
        ));
        put("Health", Arrays.asList(
            "Prevention is better than cure. Do you agree that governments should spend more money on prevention than treatment?",
            "In many countries the level of crime is increasing and crimes are becoming more violent. Why is this happening? What solutions can be taken?",
            "Some people think that competitive sports such as football should be taught in schools. Others think that children should only participate in non-competitive sports. Discuss both views."
        ));
        put("Society", Arrays.asList(
            "In many countries, the gap between the rich and the poor is widening. What problems does this create and what solutions can you suggest?",
            "Some people believe that unpaid community service should be compulsory in high school. To what extent do you agree?",
            "In some countries, young people are encouraged to work or travel for a year between finishing school and starting university. Discuss the advantages and disadvantages."
        ));
    }};

    /**
     * IELTS Reading - Common Question Types với strategies
     */
    public static final Map<String, Map<String, String>> READING_QUESTION_TYPES = new LinkedHashMap<>() {{
        put("Multiple Choice", Map.of(
            "description", "Choose the correct answer from A, B, C or D",
            "strategy", "Read the question first, identify keywords, scan the passage for those keywords",
            "tip", "Be careful of distractors - answers that are mentioned but don't answer the question"
        ));
        put("True/False/Not Given", Map.of(
            "description", "Decide if statements agree with the information in the passage",
            "strategy", "TRUE = exactly matches the passage, FALSE = contradicts the passage, NOT GIVEN = no information",
            "tip", "Don't use your own knowledge - only use information from the passage"
        ));
        put("Matching Headings", Map.of(
            "description", "Match headings to paragraphs",
            "strategy", "Read the first and last sentences of each paragraph, they usually contain the main idea",
            "tip", "Cross out headings as you use them"
        ));
        put("Sentence Completion", Map.of(
            "description", "Complete sentences using words from the passage",
            "strategy", "Use the exact words from the passage, check the word limit",
            "tip", "Grammar must be correct - check singular/plural, verb forms"
        ));
        put("Summary Completion", Map.of(
            "description", "Fill in gaps in a summary of the passage",
            "strategy", "Read the summary first to understand the context, then find the relevant section",
            "tip", "Answers usually come in order in the passage"
        ));
        put("Matching Information", Map.of(
            "description", "Match statements to paragraphs containing the information",
            "strategy", "Underline keywords in the statements, scan paragraphs for synonyms",
            "tip", "A paragraph may be used more than once or not at all"
        ));
    }};

    /**
     * IELTS Listening - Section types và tips
     */
    public static final Map<String, Map<String, Object>> LISTENING_SECTIONS = new LinkedHashMap<>() {{
        put("Section 1", Map.of(
            "context", "Social/everyday conversation (e.g., booking, enquiry)",
            "speakers", 2,
            "difficulty", "Easiest",
            "tips", Arrays.asList(
                "Listen for spelling of names and addresses",
                "Numbers and dates are common",
                "Write exactly what you hear"
            )
        ));
        put("Section 2", Map.of(
            "context", "Monologue on a general topic (e.g., tour guide, radio announcement)",
            "speakers", 1,
            "difficulty", "Easy-Medium",
            "tips", Arrays.asList(
                "Preview all questions before listening",
                "Listen for signpost words like 'firstly', 'however', 'finally'",
                "Maps and diagrams often appear"
            )
        ));
        put("Section 3", Map.of(
            "context", "Academic discussion (e.g., students and tutor)",
            "speakers", "2-4",
            "difficulty", "Medium-Hard",
            "tips", Arrays.asList(
                "Identify different speakers' opinions",
                "Listen for agreement and disagreement",
                "Academic vocabulary is common"
            )
        ));
        put("Section 4", Map.of(
            "context", "Academic lecture or presentation",
            "speakers", 1,
            "difficulty", "Hardest",
            "tips", Arrays.asList(
                "No break in the middle - preview all questions",
                "Complex academic vocabulary",
                "Focus on main ideas and key details"
            )
        ));
    }};

    /**
     * Band Score Descriptors - Official IELTS criteria
     */
    public static final Map<Double, Map<String, String>> BAND_DESCRIPTORS = new LinkedHashMap<>() {{
        put(9.0, Map.of(
            "level", "Expert",
            "description", "Fully operational command of the language",
            "writing", "Uses a wide range of structures with full flexibility and accuracy",
            "speaking", "Uses language naturally, appropriately and with precision"
        ));
        put(8.0, Map.of(
            "level", "Very Good",
            "description", "Fully operational command with only occasional inaccuracies",
            "writing", "Uses a wide range of structures with only rare errors",
            "speaking", "Develops topics coherently and appropriately"
        ));
        put(7.0, Map.of(
            "level", "Good",
            "description", "Operational command with occasional inaccuracies",
            "writing", "Uses a variety of complex structures with good control",
            "speaking", "Uses vocabulary and idioms appropriately, with some errors"
        ));
        put(6.0, Map.of(
            "level", "Competent",
            "description", "Generally effective command despite inaccuracies",
            "writing", "Uses a mix of simple and complex structures with some errors",
            "speaking", "Produces extended discourse but with hesitations"
        ));
        put(5.0, Map.of(
            "level", "Modest",
            "description", "Partial command, copes with overall meaning",
            "writing", "May produce frequent grammatical errors",
            "speaking", "Can maintain the flow of speech but with noticeable pauses"
        ));
    }};

    // ============== SERVICE METHODS ==============

    /**
     * Lấy danh sách từ vựng IELTS theo band level
     */
    public List<Map<String, Object>> getVocabularyByBand(double targetBand) {
        List<String> wordList;
        if (targetBand >= 7.0) {
            wordList = ADVANCED_VOCAB_BAND_7_8;
        } else {
            wordList = ACADEMIC_WORD_LIST_BAND_6_7;
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (String word : wordList) {
            Map<String, Object> wordData = new HashMap<>();
            wordData.put("word", word);
            wordData.put("targetBand", targetBand);
            
            // Lấy thông tin từ Dictionary API
            try {
                Object definition = vocabularyApiService.getWordDefinition(word);
                if (definition != null) {
                    wordData.put("details", definition);
                }
            } catch (Exception e) {
                log.debug("Could not fetch definition for: {}", word);
            }
            
            result.add(wordData);
        }
        return result;
    }

    /**
     * Lấy chủ đề Speaking theo Part
     */
    public Map<String, List<String>> getSpeakingTopics() {
        return SPEAKING_TOPICS;
    }

    /**
     * Lấy đề Writing Task 2 theo chủ đề
     */
    public Map<String, List<String>> getWritingTopics() {
        return WRITING_TASK2_TOPICS;
    }

    /**
     * Lấy thông tin các loại câu hỏi Reading
     */
    public Map<String, Map<String, String>> getReadingQuestionTypes() {
        return READING_QUESTION_TYPES;
    }

    /**
     * Lấy thông tin các Section trong Listening
     */
    public Map<String, Map<String, Object>> getListeningSections() {
        return LISTENING_SECTIONS;
    }

    /**
     * Lấy mô tả Band Score
     */
    public Map<Double, Map<String, String>> getBandDescriptors() {
        return BAND_DESCRIPTORS;
    }

    /**
     * Random một đề Writing theo category
     */
    public String getRandomWritingTopic(String category) {
        List<String> topics = WRITING_TASK2_TOPICS.get(category);
        if (topics != null && !topics.isEmpty()) {
            return topics.get(new Random().nextInt(topics.size()));
        }
        // Return random from any category
        List<String> allTopics = WRITING_TASK2_TOPICS.values().stream()
                .flatMap(List::stream)
                .toList();
        return allTopics.get(new Random().nextInt(allTopics.size()));
    }

    /**
     * Random một Cue Card topic cho Speaking Part 2
     */
    public String getRandomSpeakingCueCard() {
        List<String> cueCards = SPEAKING_TOPICS.get("Part 2 - Cue Card Topics");
        return cueCards.get(new Random().nextInt(cueCards.size()));
    }
}
