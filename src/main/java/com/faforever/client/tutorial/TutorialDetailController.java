package com.faforever.client.tutorial;

import com.faforever.client.fx.AbstractViewController;
import javafx.scene.Node;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TutorialDetailController extends AbstractViewController<Node> {
  @Override
  public Node getRoot() {
    return null;
  }
}
