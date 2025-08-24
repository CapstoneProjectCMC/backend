package com.codecampus.ai.helper;

import java.util.function.Consumer;
import lombok.experimental.UtilityClass;
import org.springframework.ai.chat.client.ChatClient;

@UtilityClass
public class AIGenerationHelper {

  public Consumer<ChatClient.AdvisorSpec> noMemory() {
    return adv -> adv.param("skipMemory", "true");
  }
}
