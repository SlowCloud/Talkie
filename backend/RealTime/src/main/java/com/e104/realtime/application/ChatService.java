package com.e104.realtime.application;

import com.e104.realtime.domain.ConversationAnalytics.ConversationContent;
import com.e104.realtime.domain.ConversationAnalytics.Sentiment;
import com.e104.realtime.domain.ConversationAnalytics.Vocabulary;
import com.e104.realtime.domain.ConversationAnalytics.WordCloud;
import com.e104.realtime.domain.DayAnalytics.DayAnalytics;
import com.e104.realtime.domain.WeekAnalytics.WeekWordCloud;
import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatgptService chatgptService;

    public String summarizeEmotions(List<DayAnalytics> dayAnalyticsList) {
        String message = getWeekEmotionMessage(dayAnalyticsList);
        List<MultiChatMessage> messages = Arrays.asList(
                new MultiChatMessage("system", """
                        Day1이 일요일이고, Day2가 월요일, Day3이 화요일, Day4가 수요일, Day5가 목요일, Day6이 금요일, Day7이 토요일이야.
                        
                        너는 내가 제공하는 아이의 일주일치 감정 점수를 바탕으로 부모님께 요약해줘야 해요. 아이의 감정 패턴을 이해하기 쉽게 설명해 주세요. 예를 들어:
                        
                        '아이가 일주일 동안 보인 감정을 분석해 보면, 기쁨과 놀라움에서 높은 반응을 보이며, 두려움과 슬픔에서 낮은 반응을 보여요. 이는 아이가 주로 긍정적인 감정을 더 강하게 느끼는 경향이 있음을 나타내요.'
                        
                        이와 비슷하게 감정 점수를 요약해주고, 부모님께 아이의 감정 상태에 맞춘 간단한 조언도 제안해 주세요. 예를 들어:
                        
                        '아이의 긍정적인 감정을 키워주기 위해 매일 즐거웠던 일에 대해 이야기해 보세요.' 또는 '아이에게 새로운 활동을 시도하게 하여 놀라움을 느끼게 해주는 것도 좋은 방법이에요.'
                        
                        글자수는 150자 이내로 작성해 주세요.
                        """
                ),
                new MultiChatMessage("user", message)
        );
        // multiChat 메서드를 통해 OpenAI API에 연속 메시지를 전달하고 응답을 받습니다.
        return chatgptService.multiChat(messages);
    }

    private static String getWeekEmotionMessage(List<DayAnalytics> dayAnalyticsList) {
        StringBuilder message = new StringBuilder();
        for (int i = 1; i <= dayAnalyticsList.size(); i++) {
            DayAnalytics dayAnalytics = dayAnalyticsList.get(i - 1);
            if (dayAnalytics.getHappyScore() == 0 && dayAnalytics.getLoveScore() == 0 && dayAnalytics.getSadScore() == 0 && dayAnalytics.getAngryScore() == 0 && dayAnalytics.getAmazingScore() == 0 && dayAnalytics.getScaryScore() == 0)
                continue;
            message.append("Day ").append(i).append(":\n").append("기쁨: ").append(dayAnalytics.getHappyScore()).append("\n").append("사랑스러움: ").append(dayAnalytics.getLoveScore()).append("\n").append("슬픔: ").append(dayAnalytics.getSadScore()).append("\n").append("화남: ").append(dayAnalytics.getAngryScore()).append("\n").append("놀라움: ").append(dayAnalytics.getAmazingScore()).append("\n").append("두려움: ").append(dayAnalytics.getScaryScore()).append("\n");
        }
        return message.toString();
    }

    public String summarizeVocabulary(List<DayAnalytics> dayAnalyticsList, LocalDate birth) {
        String message = getWeekVocabularyMessage(dayAnalyticsList, birth);
        List<MultiChatMessage> messages = Arrays.asList(
                new MultiChatMessage("system", """
                        Day1이 일요일이고, Day2가 월요일, Day3이 화요일, Day4가 수요일, Day5가 목요일, Day6이 금요일, Day7이 토요일이야.
                        
                        너는 내가 제공하는 아이의 일주일치 어휘력 점수를 바탕으로 부모님께 요약해줘야 해요. 아이의 어휘력 변화를 쉽게 이해할 수 있게 설명해 주세요. 예를 들어:
                        
                        '아이가 일주일 동안 보인 어휘력 점수를 보면, 어휘력이 점점 증가하는 게 보이네요. 특히 화요일에 큰 향상이 보였어요! 이번 주 어휘력 평균 점수는 4.0점으로, 동나이 어린이의 평균 점수인 3.75보다 높아요. 아이에게 칭찬해 주세요!'
                        
                        이와 비슷한 형식으로 어휘력 점수를 요약해 주고, 부모님께 아이를 격려할 방법도 간단히 제안해 주세요. 예를 들어:
                        
                        '아이가 관심을 가진 주제에 대해 질문을 해보거나, 새로운 단어를 배울 때마다 스티커를 주며 칭찬해 주세요!' 또는 '아이와 함께 동화책을 읽고 새로운 단어를 찾아보는 활동을 하시면 더욱 좋을 것 같아요!'
                        
                        글자수는 150자 이내로 작성해 주세요.
                        """
                ),
                new MultiChatMessage("user", message)
        );
        // multiChat 메서드를 통해 OpenAI API에 연속 메시지를 전달하고 응답을 받습니다.
        return chatgptService.multiChat(messages);
    }

    private String getWeekVocabularyMessage(List<DayAnalytics> dayAnalyticsList, LocalDate birth) {
        StringBuilder message = new StringBuilder();
        double sum = 0;
        int count = 0;
        for (int i = 1; i <= dayAnalyticsList.size(); i++) {
            DayAnalytics dayAnalytics = dayAnalyticsList.get(i - 1);
            if (dayAnalytics.getVocabularyScore() == 0) continue;
            message.append("Day ").append(i).append(":\n").append("어휘 점수: ").append(dayAnalytics.getVocabularyScore()).append("\n");
            sum += dayAnalytics.getVocabularyScore();
            count++;
        }
        double vocabularyScore = sum / count;
        message.append("이번 주 어휘력 평균 점수: ").append(Math.round(vocabularyScore * 100) / 100.0).append("\n");
        int age = LocalDate.now().getYear() - birth.getYear() + 1;
        int avgScore;
        if (age == 5) {
            avgScore = 4;
        } else if (age == 6) {
            avgScore = 5;
        } else if (age == 7) {
            avgScore = 6;
        } else {
            avgScore = 5;
        }
        message.append("동나이 어린이의 어휘력 평균 점수: ").append(avgScore).append("\n");
        return message.toString();
    }

    public String summarizeWeekWordCloud(List<WeekWordCloud> weekWordClouds) {
        String message = getWeekWordCloudMessage(weekWordClouds);
        List<MultiChatMessage> messages = Arrays.asList(
                new MultiChatMessage("system", """
                        너는 내가 제공하는 아이의 관심사 목록(워드클라우드)을 바탕으로 부모님께 요약해줘야 해요. 아이가 관심을 가진 주제를 분류해서 설명해 주세요. 예를 들어:
                        
                        '아이는 이번주의 주로 장난감, 가족, 계절, 음식 등이 아이의 관심을 보였어요.'
                        
                        이렇게 워드클라우드를 분석하고 요약해주고, 부모님께 아이와 어떤 활동을 하면 좋을지 제안도 해줘요. 예를 들어, '아이가 좋아하는 장난감을 함께 가지고 놀거나 놀이공원을 방문해보시는 건 어때요?'와 같이 구체적인 제안도 추가해 주세요.
                        
                        글자수는 150자 이내로 작성해 주세요.
                        """
                ),
                new MultiChatMessage("user", message)
        );
        // multiChat 메서드를 통해 OpenAI API에 연속 메시지를 전달하고 응답을 받습니다.
        return chatgptService.multiChat(messages);
    }

    private String getWeekWordCloudMessage(List<WeekWordCloud> weekWordClouds) {
        StringBuilder message = new StringBuilder();
        if (weekWordClouds.isEmpty()) {
            message.append("아이가 이번 주에 관심을 보이는 주제가 없어요.");
        }
        int size = Math.min(weekWordClouds.size(), 5); // 상위 5개만 출력
        weekWordClouds.sort((o1, o2) -> o2.getCount() - o1.getCount());
        for (int i = 1; i <= size; i++) {
            WeekWordCloud weekWordCloud = weekWordClouds.get(i - 1);
            message.append("Word ").append(i).append(":\n").append(weekWordCloud.getWord()).append(": ").append(weekWordCloud.getCount()).append("개\n");
        }
        return message.toString();
    }

    public String getConversationSummary(List<ConversationContent> conversationContents) {
        String message = getConversationSummaryMessage(conversationContents);
        List<MultiChatMessage> messages = Arrays.asList(
                new MultiChatMessage("system", """
                        주어진 대화 목록을 바탕으로 아이가 어떤 대화를 나눴는지 부모님께 설명해 주세요. 대화의 주제를 잘 드러내는 제목도 함께 만들어 주세요.
                        제목은 15자 이내로 간단하게 정해 주세요. 대화 내용은 아이가 어떤 이야기를 나눴는지 쉽게 이해할 수 있도록 간단히 요약해 주세요.
                        
                        예시:
                        대화 목록:
                        아이: "토키, 오늘 날씨가 어때?"
                        토키: "오늘은 맑고 따뜻한 날씨야!"
                        아이: "그럼 산책하러 나가볼까?"
                        토키: "좋아, 산책하면서 재밌게 놀자!"
                        
                        요약: "아이가 토키와 오늘 맑은 날씨에 대해 이야기하며 산책할 계획을 세웠어요."
                        제목: "아이와 토키의 산책 이야기"
                        
                        글자수는 150자 이내로 작성해 주세요.
                        """
                ),
                new MultiChatMessage("user", message)
        );
        // multiChat 메서드를 통해 OpenAI API에 연속 메시지를 전달하고 응답을 받습니다.
        return chatgptService.multiChat(messages);
    }

    private String getConversationSummaryMessage(List<ConversationContent> conversationContents) {
        StringBuilder message = new StringBuilder();
        for (ConversationContent conversationContent : conversationContents) {
            if (conversationContent.isAnswer()) {
                message.append("아이: ");
            } else {
                message.append("토키: ");
            }
            message.append(conversationContent.getContent()).append(" \n");
        }
        return message.toString();
    }


    public String summarizeConversationWordCloud(List<WordCloud> wordClouds) {
        String message = getConversationWordCloudMessage(wordClouds);
        List<MultiChatMessage> messages = Arrays.asList(
                new MultiChatMessage("system", """
                        너는 내가 제공하는 아이의 관심사 목록(워드클라우드)을 바탕으로 부모님께 요약해줘야 해요. 아이가 관심을 가진 주제를 분류해서 설명해 주세요. 예를 들어:
                        
                        '아이는 이번 대화에서 주로 장난감, 가족, 계절, 음식 등이 아이의 관심을 보였어요.'
                        
                        이렇게 워드클라우드를 분석하고 요약해주고, 부모님께 아이와 어떤 활동을 하면 좋을지 제안도 해줘요. 예를 들어, '아이가 좋아하는 장난감을 함께 가지고 놀거나 놀이공원을 방문해보시는 건 어때요?'와 같이 구체적인 제안도 추가해 주세요.
                        
                        글자수는 150자 이내로 작성해 주세요.
                        """
                ),
                new MultiChatMessage("user", message)
        );
        // multiChat 메서드를 통해 OpenAI API에 연속 메시지를 전달하고 응답을 받습니다.
        return chatgptService.multiChat(messages);
    }

    private String getConversationWordCloudMessage(List<WordCloud> wordClouds) {
        StringBuilder message = new StringBuilder();
        if (wordClouds.isEmpty()) {
            message.append("아이가 이번 대화에서 관심을 보이는 주제가 없어요.");
        }
        for (WordCloud wordCloud : wordClouds) {
            message.append(wordCloud.getWord()).append(": ").append(wordCloud.getCount()).append("개\n");
        }
        return message.toString();
    }

    public String summarizeConversationEmotion(Sentiment sentiment) {
        String message = getConversationEmotionMessage(sentiment);
        List<MultiChatMessage> messages = Arrays.asList(
                new MultiChatMessage("system", """
                        너는 내가 제공하는 아이의 대화 감정 분석 결과를 바탕으로 부모님께 요약해줘야 해요. 아이가 대화 중 보인 주요 감정을 설명해 주세요. 예를 들어:
                        
                        '아이가 이번 대화에서 주로 기쁨과 놀라움을 느꼈어요. 이는 아이가 대화를 통해 긍정적인 감정을 더 많이 느끼는 경향이 있음을 나타내요.'
                        
                        이렇게 감정 분석 결과를 요약해주고, 부모님께 아이의 감정 상태에 맞춘 간단한 조언도 제안해 주세요. 예를 들어:
                        
                        '아이의 긍정적인 감정을 키워주기 위해 매일 즐거웠던 일에 대해 이야기해 보세요.' 또는 '아이에게 새로운 활동을 시도하게 하여 놀라움을 느끼게 해주는 것도 좋은 방법이에요.'
                        
                        글자수는 150자 이내로 작성해 주세요.
                        """
                ),
                new MultiChatMessage("user", message)
        );
        // multiChat 메서드를 통해 OpenAI API에 연속 메시지를 전달하고 응답을 받습니다.
        return chatgptService.multiChat(messages);
    }

    private String getConversationEmotionMessage(Sentiment sentiment) {
        return "기쁨: " + sentiment.getHappyScore() + "\n" + "사랑스러움: " + sentiment.getLoveScore() + "\n" + "슬픔: " + sentiment.getSadScore() + "\n" + "화남: " + sentiment.getAngryScore() + "\n" + "놀라움: " + sentiment.getAmazingScore() + "\n" + "두려움: " + sentiment.getScaryScore() + "\n";
    }

    public String summarizeConversationVocabulary(Vocabulary vocabulary, LocalDate birth) {
        String message = getConversationVocabularyMessage(vocabulary, birth);
        List<MultiChatMessage> messages = Arrays.asList(
                new MultiChatMessage("system", """
                        제공된 아이의 어휘력 점수를 바탕으로 부모님께 쉽게 설명해 주세요. 예시 형식은 다음과 같습니다:
                        
                        '아이가 이번 대화에서 보인 어휘력 점수는 동나이대 평균 점수인 3.75보다 높습니다. 아이에게 칭찬을 아끼지 말아 주세요!'
                        
                        이와 유사한 방식으로 아이의 어휘력 점수를 요약해 주고, 부모님께 아이를 격려할 수 있는 방법도 함께 제안해 주세요. 예를 들어:
                        
                        '아이에게 관심 있는 주제에 대해 대화를 유도하거나, 새로운 단어를 배울 때마다 스티커를 주며 칭찬해 보세요.' 또는 '아이와 동화책을 읽으며 새로운 단어를 찾아보는 활동을 권해드립니다.'
                        
                        글자수는 150자 이내로 작성해 주세요.
                        """
                ),
                new MultiChatMessage("user", message)
        );
        // multiChat 메서드를 통해 OpenAI API에 연속 메시지를 전달하고 응답을 받습니다.
        return chatgptService.multiChat(messages);
    }

    private String getConversationVocabularyMessage(Vocabulary vocabulary, LocalDate birth) {
        int age = LocalDate.now().getYear() - birth.getYear() + 1;
        int avgScore;
        if (age == 5) {
            avgScore = 4;
        } else if (age == 6) {
            avgScore = 5;
        } else if (age == 7) {
            avgScore = 6;
        } else {
            avgScore = 5;
        }
        return "어휘 점수: " + vocabulary.getVocabularyScore() + "\n" +
                "동나이대 평균 어휘 점수: " + avgScore + "\n";
    }

    public String summarizeConversationCount(List<DayAnalytics> filteredDayAnalytics) {
        String message = getConversationCountMessage(filteredDayAnalytics);
        List<MultiChatMessage> messages = Arrays.asList(
                new MultiChatMessage("system", """
                        Day1이 일요일이고, Day2가 월요일, Day3이 화요일, Day4가 수요일, Day5가 목요일, Day6이 금요일, Day7이 토요일이야.
                        
                        아이가 이번 주에 몇 번의 대화를 나눴는지, 대화 횟수의 이번주 변동 추이가 어떻게 되는지 부모님께 알려주세요. 대화 횟수를 쉽게 이해할 수 있도록 요약해 주세요. 1이 일요일, 2가 월요일..7이 토요일이야. 예를 들어:
                        
                        '아이가 이번 주에 총 20번의 대화를 나누었어요. 하루 평균 대화 횟수는 3번이었어요. 대화 횟수는 월요일부터 금요일까지 점차 증가하는 추세를 보였어요. 가장 많은 대화를 나눈 날은 목요일이었어요.'
                        
                        이와 같이 대화 횟수를 요약해주세요.
                        
                        글자수는 150자 이내로 작성해 주세요.
                        """
                ),
                new MultiChatMessage("user", message)
        );
        // multiChat 메서드를 통해 OpenAI API에 연속 메시지를 전달하고 응답을 받습니다.
        return chatgptService.multiChat(messages);
    }

    private String getConversationCountMessage(List<DayAnalytics> filteredDayAnalytics) {
        // 메시지의 총 대화 횟수, 평균 대화횟수, 1주일간의 각각의 대화횟수가 포함
        StringBuilder message = new StringBuilder();
        int totalConversationCount = 0;
        for (int i = 1; i <= filteredDayAnalytics.size(); i++) {
            DayAnalytics dayAnalytics = filteredDayAnalytics.get(i - 1);
            message.append("Day ").append(i).append(":\n").append("대화 횟수: ").append(dayAnalytics.getConversationCount()).append("\n");
            totalConversationCount += dayAnalytics.getConversationCount();
        }
        message.append("이번 주 총 대화 횟수: ").append(totalConversationCount).append("\n");
        message.append("하루 평균 대화 횟수: ").append(totalConversationCount / filteredDayAnalytics.size()).append("\n");
        return message.toString();
    }


    public String getWordCloud(List<ConversationContent> conversationContents) {
        String message = getWordCloudMessage(conversationContents);
        List<MultiChatMessage> messages = Arrays.asList(
                new MultiChatMessage("system", """
                            주어진 대화 목록에서 아이가 말한 부분만을 사용해 관심사를 분석하고, "토키"가 말한 부분은 완전히 무시해줘.
                            각 관심사는 세부 주제별로 분류하고, 언급된 횟수를 포함하며, 관심도가 높은 순으로 정렬해 줘.
                            모든 항목은 독립적으로 나열해야 하며, 같은 주제라도 세부 항목으로 구분해서 각각 횟수를 표시해야 해.
                            전체적인 대화 흐름을 파악하고, 아이의 대화에서만 관심사를 뽑아내야 해.
                        
                            예시:
                            대화 목록:
                            아이: "토키, 오늘 날씨가 어때?"
                            토키: "오늘은 맑고 따뜻한 날씨야!"
                            아이: "그럼 산책하러 나가볼까?"
                            토키: "좋아, 산책하면서 재밌게 놀자!"
                        
                            출력 형식:
                            -날씨/2
                            -산책/2
                            -사자/1
                            -코끼리/1
                            -사랑/1
                        
                            정확히 이 양식과 같이 결과를 작성해 줘.
                        """),
                new MultiChatMessage("user", message)
        );
        // multiChat 메서드를 통해 OpenAI API에 연속 메시지를 전달하고 응답을 받습니다.
        return chatgptService.multiChat(messages);
    }

    private String getWordCloudMessage(List<ConversationContent> conversationContents) {
        StringBuilder message = new StringBuilder();
        for (ConversationContent conversationContent : conversationContents) {
            if (conversationContent.isAnswer()) {
                message.append("아이: ");
            } else {
                message.append("토키: ");
            }
            message.append(conversationContent.getContent()).append("\n");
        }
        return message.toString();
    }
}
