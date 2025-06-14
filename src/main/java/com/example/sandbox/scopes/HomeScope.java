package com.example.sandbox.scopes;

import com.example.sandbox.model.Item;
import com.example.sandbox.services.ItemService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Component
@ViewScoped
public class HomeScope implements Serializable {

    @Serial
    private static final long serialVersionUID = 8835282743836990468L;

    public static final String TITLE = "Configuration";
    private static final String ITEM_NAME = "name";

    private TreeNode<Item> root;
    private List<SortMeta> sortBy;
    private List<TreeNode<Item>> currentlySelectedItems;

    private final transient ItemService itemService;

    @PostConstruct
    private void init() {
        root = itemService.createItemsTree();
        currentlySelectedItems = new ArrayList<>();
        sortBy = new ArrayList<>();

        sortBy.add(SortMeta.builder()
            .field(ITEM_NAME)
            .order(SortOrder.ASCENDING)
            .build()
        );
    }

    public String getTITLE() {
        return TITLE;
    }

    /**
     * Processes the node select AJAX event.
     *
     * @param event the event object
     */
    @SuppressWarnings("unchecked")
    public void onNodeSelect(NodeSelectEvent event) {
        TreeNode<Item> node = event.getTreeNode();
        syncSelection(node, true);
    }

    /**
     * Processes the node unselect AJAX event.
     *
     * @param event the event object
     */
    @SuppressWarnings("unchecked")
    public void onNodeUnselect(NodeUnselectEvent event) {
        TreeNode<Item> node = event.getTreeNode();
        syncSelection(node, false);
    }

    /**
     * Main method to synchronize selection state in the whole tree.
     * <br><br>
     * All methods used inside skip processing of the root node. Reason:
     * <br>
     * - root node is virtual and not rendered by primefaces, therefore it's state is not visible,
     * <br>
     * - if a user selects every node in the tree, then the parent (the root node) is also selected, disabling all of its children nodes,
     * which will block the whole tree, keeping it in disabled state.
     *
     * @param node     represents the node, which the user currently interacted with
     * @param selected selection value to be applied if a node should change its selection state
     */
    private void syncSelection(TreeNode<Item> node, boolean selected) {
        List<Item> nestedItems = getNestedItems(node);
        updateNodeSelection(root, nestedItems, selected);
        selectParentsAndDisableChildren(root);
        updateCurrentlySelectedItems(); // this is needed to avoid sync issues with Primefaces treeTable component
    }

    /**
     * Recursively collects all nested data for the selected node, including the node itself,
     * so that they may be used later for selection check during processing.
     *
     * @param node node that has been selected
     * @return all nested data, including data of the selected node itself
     */
    private List<Item> getNestedItems(TreeNode<Item> node) {
        List<Item> accumulatedItems = new ArrayList<>();

        if (node == null)
            return accumulatedItems;

        accumulatedItems.add(node.getData());
        node.getChildren().forEach(child -> accumulatedItems.addAll(getNestedItems(child)));

        return accumulatedItems;
    }

    /**
     * Traverses the tree recursively to determine which node should be selected.
     * <br><br>
     * For unselect to work, the processing is done from the top of the tree down, otherwise parent nodes will block their children from unselecting.
     * <br><br>
     * Skips processing of the root node.
     *
     * @param node        this node and all nested children will be processed
     * @param itemsToSync if any processed node equals to any node in this list, it's a candidate for an update
     * @param selected    selection value to be applied if a node should change its selection state
     */
    private void updateNodeSelection(TreeNode<Item> node, List<Item> itemsToSync, boolean selected) {
        if (node == null)
            return;

        Item item = node.getData();

        // Update the node's state first...
        if (!node.equals(root) && itemsToSync.contains(item)) {
            if (selected) {
                node.setSelected(true);
            } else {
                node.setSelected(isParentSelected(node));
                node.setSelectable(true);
            }
        }

        // ...then travel down the tree
        node.getChildren().forEach(child -> updateNodeSelection(child, itemsToSync, selected));
    }

    private boolean isParentSelected(TreeNode<Item> node) {
        TreeNode<Item> parent = node.getParent();
        return parent != null && parent.isSelected();
    }

    /**
     * Recursively selects all parents and disables their children, if all children end up in selected state after initial node selection update.
     * <br><br>
     * The processing is done from the bottom of the tree up, because children need to be processed first, as parent's state depends on children's
     * state.
     * <br><br>
     * Skips processing of the root node.
     */
    private void selectParentsAndDisableChildren(TreeNode<Item> node) {
        if (node == null)
            return;

        // Travel down the tree first...
        node.getChildren().forEach(this::selectParentsAndDisableChildren);

        // ...then update the parent's and its children's state on the way up
        if (!node.equals(root) && areAllChildrenSelected(node)) {
            node.setSelected(true); // select parent
            selectAndDisableAllChildren(node);
        }
    }

    private boolean areAllChildrenSelected(TreeNode<Item> parent) {
        List<TreeNode<Item>> children = parent.getChildren();
        return !children.isEmpty() && children.stream().allMatch(TreeNode::isSelected);
    }


    private void selectAndDisableAllChildren(TreeNode<Item> parent) {
        List<Item> nestedItems = getNestedItems(parent);
        nestedItems.remove(parent.getData()); // exclude the parent
        selectAndDisableNodes(root, nestedItems);
    }

    /**
     * Selects and disables all children if they are equal to any item in items to sync list.
     * <br><br>
     * Skips processing of the root node.
     */
    private void selectAndDisableNodes(TreeNode<Item> node, List<Item> itemsToSync) {
        if (node == null)
            return;

        Item item = node.getData();
        List<TreeNode<Item>> children = node.getChildren();

        // Update the node's state first...
        if (!node.equals(root) && itemsToSync.contains(item)) {
            node.setSelected(true);
            node.setSelectable(false);
        }

        // ...then travel down the tree
        children.forEach(child -> selectAndDisableNodes(child, itemsToSync));
    }

    /**
     * Sync the UI displayed by Primefaces treeTable component with the actual tree state, represented by the root node.
     */
    private void updateCurrentlySelectedItems() {
        currentlySelectedItems.clear();
        currentlySelectedItems = collectSelectedNodes(root);
    }

    private List<TreeNode<Item>> collectSelectedNodes(TreeNode<Item> node) {
        List<TreeNode<Item>> accumulatedNodes = new ArrayList<>();

        if (node == null)
            return accumulatedNodes;

        if (node.isSelected())
            accumulatedNodes.add(node);

        node.getChildren().forEach(child -> accumulatedNodes.addAll(collectSelectedNodes(child)));

        return accumulatedNodes;
    }
}
