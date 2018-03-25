package com.faforever.client.tutorial;

import com.faforever.client.fx.AbstractViewController;
import com.faforever.client.i18n.I18n;
import com.faforever.client.main.event.NavigateEvent;
import com.faforever.client.map.MapBean;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TutorialController extends AbstractViewController<Node> {
  private final TutorialService tutorialService;
  private final I18n i18n;
  public StackPane root;
  public TreeTableView<Tutorial> treeTableTutorials;
  public TreeTableColumn<Tutorial, String> categoryTreeTableColumn;
  public TreeTableColumn<Tutorial, Number> ordinalTreeTableColumn;
  public TreeTableColumn<Tutorial, Image> imageTreeTableColumn;
  public TreeTableColumn<Tutorial, MapBean> mapTreeTableColumn;
  public TreeTableColumn<Tutorial, String> nameTreeTableColumn;
  private TreeItem<Tutorial> rootItem;

  @Inject
  public TutorialController(TutorialService tutorialService, I18n i18n) {
    this.tutorialService = tutorialService;
    this.i18n = i18n;

  }

  private void initTreeTable() {
    categoryTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().categoryProperty());
    ordinalTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().ordinalProperty());
    imageTreeTableColumn.setCellValueFactory(param -> {
      StringProperty relativePathToImage = param.getValue().getValue().imageProperty();
      //TODO:use correct Image
      return new SimpleObjectProperty<>();
    });
    mapTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().mapVersionProperty());
    nameTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().titleProperty());

    ordinalTreeTableColumn.setCellFactory(param -> new OrdinalCell());

    //Todo: define cells

    Tutorial tutorial = new Tutorial();
    tutorial.setCategory(i18n.get("tutorial.tutorial"));
    rootItem = new TreeItem<>(tutorial);
    rootItem.setExpanded(true);
    treeTableTutorials.setRoot(rootItem);
    treeTableTutorials.refresh();
  }

  @Override
  public void onDisplay(NavigateEvent navigateEvent) {
    super.onDisplay(navigateEvent);
    initTreeTable();
    tutorialService.getTutorialsSortedByCategory().thenAccept(this::displayTutorials);
  }

  private void displayTutorials(Map<String, List<Tutorial>> tutorials) {
    tutorials.keySet().forEach(this::createCategoryItem);
    tutorials.forEach((key, value) -> value.forEach(this::createTutorialItem));
    treeTableTutorials.refresh();
  }

  private void createTutorialItem(Tutorial tutorial) {
    TreeItem<Tutorial> tutorialTreeItem = new TreeItem<>();
    tutorialTreeItem.setValue(tutorial);
    Optional<TreeItem<Tutorial>> parentItem = rootItem.getChildren().stream().filter(tutorialTreeItem1 -> tutorialTreeItem1.getValue().getCategory().equals(tutorial.getCategory())).findFirst();
    parentItem.get().getChildren().add(tutorialTreeItem);
  }

  private void createCategoryItem(String category) {
    TreeItem<Tutorial> tutorialTreeItem = new TreeItem<>();
    Tutorial tutorial = new Tutorial();
    try {
      tutorial.setCategory(i18n.get(category));
    } catch (Exception e) {
      log.warn("Tutorial category is no message key", e);
      tutorial.setCategory(category);
    }
    tutorialTreeItem.setValue(tutorial);
    tutorialTreeItem.setExpanded(true);
    rootItem.getChildren().add(tutorialTreeItem);
  }

  @Override
  public Node getRoot() {
    return root;
  }

  public final class OrdinalCell extends TreeTableCell<Tutorial, Number> {

    @Override
    protected void updateItem(Number item, boolean empty) {
      super.updateItem(item, empty);
      if (empty || item.equals(0)) {
        setGraphic(null);
      } else {
        setGraphic(new Label(item.toString()));
      }
    }
  }
}
