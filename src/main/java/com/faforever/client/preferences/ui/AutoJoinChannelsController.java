package com.faforever.client.preferences.ui;

import com.faforever.client.fx.Controller;
import com.faforever.client.preferences.PreferencesService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AutoJoinChannelsController implements Controller<Node> {
  private final PreferencesService preferencesService;
  @FXML
  public TextField channelTextField;
  public Button addChannelButton;
  public ListView<String> channelListView;
  public GridPane autoJoinChannelsSettingsRoot;

  @Inject
  public AutoJoinChannelsController(PreferencesService preferencesService) {
    this.preferencesService = preferencesService;
  }

  public void initialize() {
    channelListView.setOnMouseClicked(event -> {
      preferencesService.getPreferences().getChat().removeAutoJoinChannel((channelListView.getSelectionModel().getSelectedItem()));
      channelListView.getSelectionModel().clearSelection();
      preferencesService.storeInBackground();
    });
    channelListView.setItems(preferencesService.getPreferences().getChat().getAutoJoinChannels());
  }

  public Node getRoot() {
    return autoJoinChannelsSettingsRoot;
  }

  public void onAddChannelButtonPressed() {
    if (channelTextField.getText().isEmpty() || channelListView.getItems().contains(channelTextField.getText())) {
      return;
    }
    preferencesService.getPreferences().getChat().addAutoJoinChannel(channelTextField.getText());
    preferencesService.storeInBackground();
  }
}
