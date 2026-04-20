package com.doit.config;

import com.doit.entity.*;
import com.doit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final ListeningAudioRepository listeningAudioRepository;
    private final ListeningQuestionRepository listeningQuestionRepository;
    private final ReadingPassageRepository readingPassageRepository;
    private final ReadingQuestionRepository readingQuestionRepository;
    private final WritingTaskRepository writingTaskRepository;
    private final SpeakingPartRepository speakingPartRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Checking if sample data needs to be initialized...");
        
        if (userRepository.count() == 0) {
            initUsers();
        } else {
            log.info("Users already exist, skipping user initialization.");
        }
        
        if (examRepository.count() == 0) {
            initListeningExams();
            initReadingExams();
            initWritingExams();
            initSpeakingExams();
            log.info("Sample exam data initialized successfully!");
        } else {
            log.info("Exams already exist, skipping exam initialization.");
        }
    }

    private void initUsers() {
        // Admin user
        User admin = User.builder()
                .email("admin@doit.io")
                .password(passwordEncoder.encode("Admin123!"))
                .fullName("DOIT Admin")
                .role(User.Role.ADMIN)
                .isActive(true)
                .isEmailVerified(true)
                .placementTestCompleted(true)
                .currentBand(9.0)
                .targetBand(9.0)
                .build();
        userRepository.save(admin);

        // Demo user
        User demo = User.builder()
                .email("demo@doit.io")
                .password(passwordEncoder.encode("Demo123!"))
                .fullName("Demo User")
                .role(User.Role.USER)
                .isActive(true)
                .isEmailVerified(true)
                .placementTestCompleted(true)
                .currentBand(5.5)
                .targetBand(7.0)
                .build();
        userRepository.save(demo);

        log.info("Created admin and demo users");
    }

    private void initListeningExams() {
        // LISTENING EXAM 1 - Band 5-6
        Exam listeningExam1 = Exam.builder()
                .title("IELTS Listening Practice Test 1")
                .description("Complete listening test with 4 sections covering everyday conversations and academic lectures.")
                .skill(Exam.Skill.LISTENING)
                .bandLevel(Exam.BandLevel.ELEMENTARY)
                .type(Exam.ExamType.PRACTICE)
                .durationMinutes(30)
                .totalQuestions(40)
                .isActive(true)
                .build();
        examRepository.save(listeningExam1);

        // Section 1 - Social conversation
        ListeningAudio audio1 = ListeningAudio.builder()
                .examId(listeningExam1.getId())
                .partNumber(1)
                .partTitle("Section 1: Booking a Hotel Room")
                .audioUrl("/audio/listening/test1/section1.mp3")
                .transcript("Receptionist: Good morning, Grand Hotel. How can I help you?\nCaller: Hi, I'd like to book a room for next weekend, please.\nReceptionist: Certainly. What dates are you looking at?\nCaller: From Friday the 15th to Sunday the 17th.\nReceptionist: Let me check availability... We have a standard double room at $120 per night or a deluxe suite at $200.\nCaller: The standard room is fine. My name is Sarah Mitchell, that's M-I-T-C-H-E-L-L.\nReceptionist: And your phone number?\nCaller: It's 07845 329167.\nReceptionist: Perfect. Would you like breakfast included? It's an extra $15 per person.\nCaller: Yes, please. For two people.\nReceptionist: Great. Your total comes to $300. We require a credit card to hold the reservation...")
                .durationSeconds(180)
                .build();
        listeningAudioRepository.save(audio1);

        // Questions for Section 1
        List<ListeningQuestion> section1Questions = Arrays.asList(
                ListeningQuestion.builder()
                        .audioId(audio1.getId())
                        .orderNumber(1)
                        .questionType(ListeningQuestion.QuestionType.FILL_IN_THE_BLANK)
                        .questionText("Guest name: Sarah _______")
                        .correctAnswer("Mitchell")
                        .explanation("The caller spells out her surname: M-I-T-C-H-E-L-L")
                        .build(),
                ListeningQuestion.builder()
                        .audioId(audio1.getId())
                        .orderNumber(2)
                        .questionType(ListeningQuestion.QuestionType.FILL_IN_THE_BLANK)
                        .questionText("Phone number: _______")
                        .correctAnswer("07845 329167")
                        .explanation("The caller provides her phone number as 07845 329167")
                        .build(),
                ListeningQuestion.builder()
                        .audioId(audio1.getId())
                        .orderNumber(3)
                        .questionType(ListeningQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("What type of room did the guest book?")
                        .correctAnswer("A")
                        .options(Arrays.asList("A. Standard double room", "B. Deluxe suite", "C. Single room", "D. Family room"))
                        .explanation("The caller chose the standard double room at $120 per night")
                        .build(),
                ListeningQuestion.builder()
                        .audioId(audio1.getId())
                        .orderNumber(4)
                        .questionType(ListeningQuestion.QuestionType.FILL_IN_THE_BLANK)
                        .questionText("Check-in date: Friday the _______")
                        .correctAnswer("15th")
                        .explanation("The caller wants to check in on Friday the 15th")
                        .build(),
                ListeningQuestion.builder()
                        .audioId(audio1.getId())
                        .orderNumber(5)
                        .questionType(ListeningQuestion.QuestionType.FILL_IN_THE_BLANK)
                        .questionText("Total cost: $_______")
                        .correctAnswer("300")
                        .explanation("2 nights at $120 = $240, plus breakfast $15 x 2 people x 2 days = $60. Total = $300")
                        .build()
        );
        listeningQuestionRepository.saveAll(section1Questions);

        // Section 2 - Monologue about local facilities
        ListeningAudio audio2 = ListeningAudio.builder()
                .examId(listeningExam1.getId())
                .partNumber(2)
                .partTitle("Section 2: University Campus Tour")
                .audioUrl("/audio/listening/test1/section2.mp3")
                .transcript("Welcome to Greenfield University! I'm your guide today, and I'll be showing you around our main campus. We're currently standing in front of the Library, which was built in 1985 and renovated last year. It's open from 8 AM to 10 PM on weekdays and 9 AM to 6 PM on weekends. Moving to your left, you'll see the Science Building, home to our award-winning Chemistry and Physics departments. Behind that is the new Sports Complex, completed in 2023, featuring an Olympic-size swimming pool, three basketball courts, and a fully equipped gym. Membership is free for all students...")
                .durationSeconds(240)
                .build();
        listeningAudioRepository.save(audio2);

        List<ListeningQuestion> section2Questions = Arrays.asList(
                ListeningQuestion.builder()
                        .audioId(audio2.getId())
                        .orderNumber(6)
                        .questionType(ListeningQuestion.QuestionType.FILL_IN_THE_BLANK)
                        .questionText("The Library was built in _______")
                        .correctAnswer("1985")
                        .explanation("The guide mentions the Library was built in 1985")
                        .build(),
                ListeningQuestion.builder()
                        .audioId(audio2.getId())
                        .orderNumber(7)
                        .questionType(ListeningQuestion.QuestionType.FILL_IN_THE_BLANK)
                        .questionText("Weekend library hours: _______ AM to 6 PM")
                        .correctAnswer("9")
                        .explanation("The library opens at 9 AM on weekends")
                        .build(),
                ListeningQuestion.builder()
                        .audioId(audio2.getId())
                        .orderNumber(8)
                        .questionType(ListeningQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("The Sports Complex was completed in:")
                        .correctAnswer("C")
                        .options(Arrays.asList("A. 1985", "B. 2020", "C. 2023", "D. 2024"))
                        .explanation("The Sports Complex was completed in 2023")
                        .build(),
                ListeningQuestion.builder()
                        .audioId(audio2.getId())
                        .orderNumber(9)
                        .questionType(ListeningQuestion.QuestionType.FILL_IN_THE_BLANK)
                        .questionText("The Sports Complex has _______ basketball courts")
                        .correctAnswer("three/3")
                        .explanation("The guide mentions three basketball courts")
                        .build(),
                ListeningQuestion.builder()
                        .audioId(audio2.getId())
                        .orderNumber(10)
                        .questionType(ListeningQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("Student membership to the Sports Complex is:")
                        .correctAnswer("A")
                        .options(Arrays.asList("A. Free", "B. $50 per month", "C. $100 per year", "D. Half price"))
                        .explanation("Membership is free for all students")
                        .build()
        );
        listeningQuestionRepository.saveAll(section2Questions);

        log.info("Created Listening Exam 1 with {} questions", section1Questions.size() + section2Questions.size());

        // LISTENING EXAM 2 - Band 6.5-7.5
        Exam listeningExam2 = Exam.builder()
                .title("IELTS Listening Practice Test 2")
                .description("Academic listening test focusing on lectures and discussions.")
                .skill(Exam.Skill.LISTENING)
                .bandLevel(Exam.BandLevel.INTERMEDIATE)
                .type(Exam.ExamType.PRACTICE)
                .durationMinutes(30)
                .totalQuestions(40)
                .isActive(true)
                .build();
        examRepository.save(listeningExam2);

        log.info("Created Listening Exam 2");
    }

    private void initReadingExams() {
        // READING EXAM 1 - Academic
        Exam readingExam1 = Exam.builder()
                .title("IELTS Academic Reading Test 1")
                .description("Three passages testing skimming, scanning, and detailed comprehension skills.")
                .skill(Exam.Skill.READING)
                .bandLevel(Exam.BandLevel.ELEMENTARY)
                .type(Exam.ExamType.ACADEMIC)
                .durationMinutes(60)
                .totalQuestions(40)
                .isActive(true)
                .build();
        examRepository.save(readingExam1);

        // Passage 1
        ReadingPassage passage1 = ReadingPassage.builder()
                .examId(readingExam1.getId())
                .passageNumber(1)
                .title("The History of Coffee")
                .passageText("""
                    Coffee is one of the world's most popular beverages, consumed by millions of people every day. But where did this beloved drink originate, and how did it spread across the globe?
                    
                    The story of coffee begins in Ethiopia, where legend has it that a goat herder named Kaldi discovered the energizing effects of coffee beans around 850 AD. According to the tale, Kaldi noticed that his goats became unusually energetic after eating berries from a certain tree. Curious, he tried the berries himself and experienced a similar boost in energy.
                    
                    From Ethiopia, coffee spread to Yemen in the 15th century, where Sufi monks began cultivating the plant and using it to stay awake during long hours of prayer. The port city of Mocha became the center of the coffee trade, giving its name to the popular coffee-chocolate drink we know today.
                    
                    By the 16th century, coffee had reached Persia, Egypt, Syria, and Turkey. Coffee houses, known as 'qahveh khaneh,' became important social gathering places where people would drink coffee, listen to music, watch performances, play chess, and discuss news. These establishments became known as 'Schools of the Wise' because of the intellectual conversations that took place there.
                    
                    Coffee arrived in Europe in the 17th century, initially meeting resistance from some who called it the 'bitter invention of Satan.' However, Pope Clement VIII, after tasting the beverage, gave it papal approval, and coffee houses soon became popular throughout Europe. By the mid-17th century, there were over 300 coffee houses in London alone.
                    
                    Today, coffee is grown in over 70 countries, with Brazil being the world's largest producer, followed by Vietnam, Colombia, and Indonesia. The global coffee industry is worth over $100 billion annually and employs millions of people worldwide.
                    """)
                .wordCount(320)
                .build();
        readingPassageRepository.save(passage1);

        // Questions for Passage 1
        List<ReadingQuestion> passage1Questions = Arrays.asList(
                ReadingQuestion.builder()
                        .passageId(passage1.getId())
                        .orderNumber(1)
                        .questionType(ReadingQuestion.QuestionType.TRUE_FALSE_NOT_GIVEN)
                        .questionText("Coffee was first discovered in Yemen.")
                        .correctAnswer("FALSE")
                        .explanation("The passage states coffee originated in Ethiopia, not Yemen.")
                        .build(),
                ReadingQuestion.builder()
                        .passageId(passage1.getId())
                        .orderNumber(2)
                        .questionType(ReadingQuestion.QuestionType.TRUE_FALSE_NOT_GIVEN)
                        .questionText("Kaldi was a farmer who grew coffee plants.")
                        .correctAnswer("FALSE")
                        .explanation("Kaldi was a goat herder, not a farmer.")
                        .build(),
                ReadingQuestion.builder()
                        .passageId(passage1.getId())
                        .orderNumber(3)
                        .questionType(ReadingQuestion.QuestionType.TRUE_FALSE_NOT_GIVEN)
                        .questionText("The mocha drink is named after a port city.")
                        .correctAnswer("TRUE")
                        .explanation("The passage mentions Mocha was a port city that gave its name to the drink.")
                        .build(),
                ReadingQuestion.builder()
                        .passageId(passage1.getId())
                        .orderNumber(4)
                        .questionType(ReadingQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("According to the passage, Sufi monks used coffee to:")
                        .correctAnswer("B")
                        .options(Arrays.asList("A. Trade with other countries", "B. Stay awake during prayer", "C. Cure diseases", "D. Entertain guests"))
                        .explanation("The passage states monks used coffee to stay awake during long hours of prayer.")
                        .build(),
                ReadingQuestion.builder()
                        .passageId(passage1.getId())
                        .orderNumber(5)
                        .questionType(ReadingQuestion.QuestionType.SENTENCE_COMPLETION)
                        .questionText("Coffee houses in the Middle East were nicknamed '_______' because of intellectual discussions.")
                        .correctAnswer("Schools of the Wise")
                        .explanation("The passage mentions they were called 'Schools of the Wise'")
                        .build(),
                ReadingQuestion.builder()
                        .passageId(passage1.getId())
                        .orderNumber(6)
                        .questionType(ReadingQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("Who gave coffee papal approval in Europe?")
                        .correctAnswer("C")
                        .options(Arrays.asList("A. Pope Francis", "B. Pope Benedict", "C. Pope Clement VIII", "D. Pope John Paul"))
                        .explanation("Pope Clement VIII gave coffee papal approval after tasting it.")
                        .build(),
                ReadingQuestion.builder()
                        .passageId(passage1.getId())
                        .orderNumber(7)
                        .questionType(ReadingQuestion.QuestionType.SENTENCE_COMPLETION)
                        .questionText("By the mid-17th century, London had over _______ coffee houses.")
                        .correctAnswer("300")
                        .explanation("The passage states there were over 300 coffee houses in London.")
                        .build(),
                ReadingQuestion.builder()
                        .passageId(passage1.getId())
                        .orderNumber(8)
                        .questionType(ReadingQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("Which country is the world's largest coffee producer?")
                        .correctAnswer("A")
                        .options(Arrays.asList("A. Brazil", "B. Vietnam", "C. Colombia", "D. Indonesia"))
                        .explanation("Brazil is mentioned as the world's largest producer.")
                        .build()
        );
        readingQuestionRepository.saveAll(passage1Questions);

        // Passage 2
        ReadingPassage passage2 = ReadingPassage.builder()
                .examId(readingExam1.getId())
                .passageNumber(2)
                .title("Climate Change and Ocean Ecosystems")
                .passageText("""
                    The world's oceans are undergoing dramatic changes due to climate change, with far-reaching consequences for marine ecosystems and the billions of people who depend on them for food and livelihoods.
                    
                    Ocean temperatures have risen by approximately 0.7°C since 1900, a seemingly small change that has had profound effects on marine life. Warmer waters hold less oxygen, creating 'dead zones' where fish and other marine creatures cannot survive. The number of these oxygen-depleted areas has quadrupled since 1950, now covering an area roughly the size of the European Union.
                    
                    Coral reefs, often called the 'rainforests of the sea' because of their incredible biodiversity, are particularly vulnerable to rising temperatures. When water temperatures exceed normal levels for extended periods, corals expel the algae living in their tissues, causing them to turn white in a process known as bleaching. If conditions don't improve, the coral dies. Scientists estimate that 70-90% of the world's coral reefs will disappear if global temperatures rise by 1.5°C above pre-industrial levels.
                    
                    Ocean acidification is another major threat. The oceans absorb about 30% of the carbon dioxide released into the atmosphere, causing the water to become more acidic. Since the Industrial Revolution, ocean acidity has increased by 30%. This makes it difficult for shellfish, corals, and some plankton to build their calcium carbonate shells and skeletons.
                    
                    Rising sea levels, caused by thermal expansion and melting ice sheets, threaten coastal ecosystems such as mangroves and salt marshes. These habitats serve as nurseries for many fish species and protect coastlines from storms. Their loss could have devastating effects on fisheries and coastal communities.
                    
                    Despite these challenges, there is hope. Marine protected areas, sustainable fishing practices, and efforts to reduce carbon emissions can help ocean ecosystems adapt and recover. However, urgent action is needed to prevent irreversible damage to our planet's life-support system.
                    """)
                .wordCount(340)
                .build();
        readingPassageRepository.save(passage2);

        List<ReadingQuestion> passage2Questions = Arrays.asList(
                ReadingQuestion.builder()
                        .passageId(passage2.getId())
                        .orderNumber(9)
                        .questionType(ReadingQuestion.QuestionType.SENTENCE_COMPLETION)
                        .questionText("Ocean temperatures have risen by approximately _______°C since 1900.")
                        .correctAnswer("0.7")
                        .explanation("The passage states temperatures have risen by 0.7°C")
                        .build(),
                ReadingQuestion.builder()
                        .passageId(passage2.getId())
                        .orderNumber(10)
                        .questionType(ReadingQuestion.QuestionType.TRUE_FALSE_NOT_GIVEN)
                        .questionText("Dead zones in the ocean have doubled since 1950.")
                        .correctAnswer("FALSE")
                        .explanation("The passage says they have quadrupled, not doubled.")
                        .build(),
                ReadingQuestion.builder()
                        .passageId(passage2.getId())
                        .orderNumber(11)
                        .questionType(ReadingQuestion.QuestionType.SENTENCE_COMPLETION)
                        .questionText("Coral reefs are often called the '_______ of the sea'.")
                        .correctAnswer("rainforests")
                        .explanation("The passage refers to them as 'rainforests of the sea'")
                        .build(),
                ReadingQuestion.builder()
                        .passageId(passage2.getId())
                        .orderNumber(12)
                        .questionType(ReadingQuestion.QuestionType.MULTIPLE_CHOICE)
                        .questionText("What happens during coral bleaching?")
                        .correctAnswer("B")
                        .options(Arrays.asList("A. Corals absorb more algae", "B. Corals expel algae from their tissues", "C. Corals change color to blue", "D. Corals grow faster"))
                        .explanation("During bleaching, corals expel the algae living in their tissues.")
                        .build(),
                ReadingQuestion.builder()
                        .passageId(passage2.getId())
                        .orderNumber(13)
                        .questionType(ReadingQuestion.QuestionType.SENTENCE_COMPLETION)
                        .questionText("The oceans absorb about _______% of atmospheric CO2.")
                        .correctAnswer("30")
                        .explanation("The passage states oceans absorb about 30% of CO2")
                        .build()
        );
        readingQuestionRepository.saveAll(passage2Questions);

        log.info("Created Reading Exam 1 with {} questions", passage1Questions.size() + passage2Questions.size());
    }

    private void initWritingExams() {
        // WRITING EXAM 1 - Academic
        Exam writingExam1 = Exam.builder()
                .title("IELTS Academic Writing Test 1")
                .description("Complete writing test with Task 1 (graph description) and Task 2 (essay).")
                .skill(Exam.Skill.WRITING)
                .bandLevel(Exam.BandLevel.ELEMENTARY)
                .type(Exam.ExamType.ACADEMIC)
                .durationMinutes(60)
                .totalQuestions(2)
                .isActive(true)
                .build();
        examRepository.save(writingExam1);

        // Task 1 - Graph description
        WritingTask task1 = WritingTask.builder()
                .examId(writingExam1.getId())
                .taskType(WritingTask.TaskType.TASK_1_ACADEMIC)
                .title("Bar Chart: Internet Usage by Age Group")
                .taskDescription("""
                    The bar chart below shows the percentage of adults in five different age groups who used the internet daily in 2010 and 2020.
                    
                    Summarize the information by selecting and reporting the main features, and make comparisons where relevant.
                    
                    Write at least 150 words.
                    """)
                .imageUrl("/images/writing/task1_internet_usage.png")
                .chartData("""
                    {
                        "type": "bar",
                        "labels": ["18-24", "25-34", "35-44", "45-54", "55+"],
                        "datasets": [
                            {"label": "2010", "data": [75, 65, 50, 35, 20]},
                            {"label": "2020", "data": [95, 90, 82, 70, 55]}
                        ]
                    }
                    """)
                .minWords(150)
                .maxWords(200)
                .timeLimitMinutes(20)
                .sampleAnswer("""
                    The bar chart illustrates the proportion of adults who used the internet on a daily basis across five age categories in 2010 and 2020.
                    
                    Overall, internet usage increased significantly across all age groups over the decade, with younger demographics maintaining higher usage rates throughout.
                    
                    In 2010, the 18-24 age group had the highest daily internet usage at 75%, followed by the 25-34 group at 65%. Usage decreased progressively with age, with only 20% of those aged 55 and over using the internet daily.
                    
                    By 2020, dramatic increases were observed in all categories. The youngest group reached near-universal usage at 95%, while the 25-34 and 35-44 groups rose to 90% and 82% respectively. Most notably, the 55+ demographic more than doubled their usage to 55%, representing the largest proportional increase.
                    
                    The data suggests that while age-related differences in internet usage persisted, the digital divide narrowed considerably between 2010 and 2020.
                    """)
                .build();
        writingTaskRepository.save(task1);

        // Task 2 - Essay
        WritingTask task2 = WritingTask.builder()
                .examId(writingExam1.getId())
                .taskType(WritingTask.TaskType.TASK_2)
                .title("Essay: Technology and Education")
                .taskDescription("""
                    Some people believe that technology has made it easier for students to learn, while others argue that it has created more distractions.
                    
                    Discuss both views and give your own opinion.
                    
                    Write at least 250 words.
                    """)
                .minWords(250)
                .maxWords(350)
                .timeLimitMinutes(40)
                .sampleAnswer("""
                    The role of technology in education has become a subject of considerable debate. While some argue that technological advancements have revolutionized learning, others contend that they have introduced unprecedented distractions. This essay will examine both perspectives before presenting my own view.
                    
                    Proponents of educational technology highlight several compelling advantages. Firstly, digital resources provide students with instant access to vast amounts of information, enabling self-directed learning. Online platforms like Khan Academy and Coursera offer free courses from prestigious institutions, democratizing education. Furthermore, interactive tools such as educational apps and virtual reality can make abstract concepts more tangible and engaging, potentially improving comprehension and retention.
                    
                    However, critics raise valid concerns about technology's negative impacts on learning. The constant connectivity offered by smartphones and laptops creates numerous opportunities for distraction, from social media notifications to online gaming. Research has shown that multitasking between studying and digital entertainment significantly reduces academic performance. Additionally, excessive screen time may impair students' ability to concentrate on traditional reading and writing tasks.
                    
                    In my opinion, technology itself is neither inherently beneficial nor harmful to education; rather, its impact depends on how it is used. When integrated thoughtfully into curricula and combined with digital literacy education, technology can enhance learning outcomes. However, this requires educators and parents to establish clear boundaries and teach students to use technology responsibly.
                    
                    In conclusion, while technology presents both opportunities and challenges for education, its ultimate value lies in our ability to harness its potential while mitigating its drawbacks.
                    """)
                .build();
        writingTaskRepository.save(task2);

        log.info("Created Writing Exam 1 with 2 tasks");

        // WRITING EXAM 2 - General Training
        Exam writingExam2 = Exam.builder()
                .title("IELTS General Training Writing Test 1")
                .description("Letter writing and essay for General Training module.")
                .skill(Exam.Skill.WRITING)
                .bandLevel(Exam.BandLevel.ELEMENTARY)
                .type(Exam.ExamType.GENERAL)
                .durationMinutes(60)
                .totalQuestions(2)
                .isActive(true)
                .build();
        examRepository.save(writingExam2);

        WritingTask letterTask = WritingTask.builder()
                .examId(writingExam2.getId())
                .taskType(WritingTask.TaskType.TASK_1_GENERAL)
                .title("Formal Letter: Job Application")
                .taskDescription("""
                    You recently saw an advertisement for a job as a tour guide in your city.
                    
                    Write a letter to the tourism company. In your letter:
                    - explain why you are interested in this job
                    - describe any relevant experience you have
                    - ask about the working hours and salary
                    
                    Write at least 150 words.
                    """)
                .minWords(150)
                .maxWords(200)
                .timeLimitMinutes(20)
                .build();
        writingTaskRepository.save(letterTask);

        log.info("Created Writing Exam 2");
    }

    private void initSpeakingExams() {
        // SPEAKING EXAM 1
        Exam speakingExam1 = Exam.builder()
                .title("IELTS Speaking Practice Test 1")
                .description("Complete speaking test with all three parts: Introduction, Long Turn, and Discussion.")
                .skill(Exam.Skill.SPEAKING)
                .bandLevel(Exam.BandLevel.ELEMENTARY)
                .type(Exam.ExamType.PRACTICE)
                .durationMinutes(15)
                .totalQuestions(3)
                .isActive(true)
                .build();
        examRepository.save(speakingExam1);

        // Part 1 - Introduction
        SpeakingPart part1 = SpeakingPart.builder()
                .examId(speakingExam1.getId())
                .partType(SpeakingPart.PartType.PART_1)
                .topic("Home and Accommodation")
                .description("The examiner will ask you general questions about yourself and familiar topics.")
                .questions(Arrays.asList(
                        "Let's talk about your home. Do you live in a house or an apartment?",
                        "What is your favorite room in your home? Why?",
                        "How long have you lived in your current home?",
                        "What would you like to change about your home?",
                        "Do you think you will live there for a long time?"
                ))
                .preparationTimeSeconds(0)
                .speakingTimeSeconds(300)
                .build();
        speakingPartRepository.save(part1);

        // Part 2 - Long Turn
        SpeakingPart part2 = SpeakingPart.builder()
                .examId(speakingExam1.getId())
                .partType(SpeakingPart.PartType.PART_2)
                .topic("A Memorable Journey")
                .description("You will be given a topic card and have 1 minute to prepare. Then speak for 1-2 minutes.")
                .cueCard("""
                    Describe a memorable journey you have taken.
                    
                    You should say:
                    - where you went
                    - how you traveled
                    - who you went with
                    
                    And explain why this journey was memorable.
                    """)
                .questions(Arrays.asList(
                        "Describe a memorable journey you have taken."
                ))
                .preparationTimeSeconds(60)
                .speakingTimeSeconds(120)
                .build();
        speakingPartRepository.save(part2);

        // Part 3 - Discussion
        SpeakingPart part3 = SpeakingPart.builder()
                .examId(speakingExam1.getId())
                .partType(SpeakingPart.PartType.PART_3)
                .topic("Travel and Tourism")
                .description("The examiner will ask more abstract questions related to the Part 2 topic.")
                .questions(Arrays.asList(
                        "Why do you think people enjoy traveling?",
                        "How has tourism changed in your country over the years?",
                        "What are the advantages and disadvantages of mass tourism?",
                        "Do you think virtual travel experiences will replace real travel in the future?",
                        "How can tourism be made more sustainable?"
                ))
                .preparationTimeSeconds(0)
                .speakingTimeSeconds(300)
                .build();
        speakingPartRepository.save(part3);

        log.info("Created Speaking Exam 1 with 3 parts");

        // SPEAKING EXAM 2
        Exam speakingExam2 = Exam.builder()
                .title("IELTS Speaking Practice Test 2")
                .description("Speaking practice focusing on technology and modern life.")
                .skill(Exam.Skill.SPEAKING)
                .bandLevel(Exam.BandLevel.INTERMEDIATE)
                .type(Exam.ExamType.PRACTICE)
                .durationMinutes(15)
                .totalQuestions(3)
                .isActive(true)
                .build();
        examRepository.save(speakingExam2);

        SpeakingPart tech1 = SpeakingPart.builder()
                .examId(speakingExam2.getId())
                .partType(SpeakingPart.PartType.PART_1)
                .topic("Technology")
                .description("Questions about your use of technology.")
                .questions(Arrays.asList(
                        "How often do you use your smartphone?",
                        "What apps do you use most frequently?",
                        "Do you think you spend too much time on electronic devices?",
                        "What was the first electronic device you owned?"
                ))
                .preparationTimeSeconds(0)
                .speakingTimeSeconds(300)
                .build();
        speakingPartRepository.save(tech1);

        log.info("Created Speaking Exam 2");
    }
}
