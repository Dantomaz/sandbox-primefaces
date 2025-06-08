package com.example.sandbox.services;

import com.example.sandbox.model.Item;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    /**
     * Create the tree structure for primefaces tree table.
     *
     * @return the root node of the tree; primefaces treats the root node as virtual and does not display it, therefore it contains no item data
     */
    public TreeNode<Item> createItemsTree() {
        DefaultTreeNode<Item> root = new DefaultTreeNode<>();

        DefaultTreeNode<Item> organisationA = new DefaultTreeNode<>(new Item("Organisation A"), root);
        DefaultTreeNode<Item> organisationB = new DefaultTreeNode<>(new Item("Organisation B"), root);

        DefaultTreeNode<Item> divisionA = new DefaultTreeNode<>(new Item("Division A"), organisationA);
        DefaultTreeNode<Item> divisionB = new DefaultTreeNode<>(new Item("Division B"), organisationA);

        DefaultTreeNode<Item> teamA = new DefaultTreeNode<>(new Item("Team A"), organisationA);
        DefaultTreeNode<Item> teamB = new DefaultTreeNode<>(new Item("Team B"), divisionA);
        DefaultTreeNode<Item> teamC = new DefaultTreeNode<>(new Item("Team C"), divisionA);
        DefaultTreeNode<Item> teamD = new DefaultTreeNode<>(new Item("Team D"), divisionB);
        DefaultTreeNode<Item> teamE = new DefaultTreeNode<>(new Item("Team E"), organisationB);

        Item employee1 = new Item("Employee 1");
        DefaultTreeNode<Item> employee1_1 = new DefaultTreeNode<>(employee1, teamA);
        DefaultTreeNode<Item> employee1_2 = new DefaultTreeNode<>(employee1, teamB);
        DefaultTreeNode<Item> employee1_3 = new DefaultTreeNode<>(employee1, teamE);
        DefaultTreeNode<Item> employee2 = new DefaultTreeNode<>(new Item("Employee 2"), teamA);
        DefaultTreeNode<Item> employee3 = new DefaultTreeNode<>(new Item("Employee 3"), teamA);
        Item employee4 = new Item("Employee 4");
        DefaultTreeNode<Item> employee4_1 = new DefaultTreeNode<>(employee4, teamB);
        DefaultTreeNode<Item> employee4_2 = new DefaultTreeNode<>(employee4, teamD);
        DefaultTreeNode<Item> employee5 = new DefaultTreeNode<>(new Item("Employee 5"), teamB);
        DefaultTreeNode<Item> employee6 = new DefaultTreeNode<>(new Item("Employee 6"), teamC);
        DefaultTreeNode<Item> employee7 = new DefaultTreeNode<>(new Item("Employee 7"), teamC);
        DefaultTreeNode<Item> employee8 = new DefaultTreeNode<>(new Item("Employee 8"), teamC);
        Item employee9 = new Item("Employee 9");
        DefaultTreeNode<Item> employee9_1 = new DefaultTreeNode<>(employee9, teamA);
        DefaultTreeNode<Item> employee9_2 = new DefaultTreeNode<>(employee9, teamB);
        DefaultTreeNode<Item> employee9_3 = new DefaultTreeNode<>(employee9, teamC);
        Item employee10 = new Item("Employee 10");
        DefaultTreeNode<Item> employee10_1 = new DefaultTreeNode<>(employee10, teamA);
        DefaultTreeNode<Item> employee10_2 = new DefaultTreeNode<>(employee10, teamD);
        DefaultTreeNode<Item> employee11 = new DefaultTreeNode<>(new Item("Employee 11"), teamD);
        DefaultTreeNode<Item> employee12 = new DefaultTreeNode<>(new Item("Employee 12"), teamE);
        DefaultTreeNode<Item> employee13 = new DefaultTreeNode<>(new Item("Employee 13"), teamE);

        return root;
    }
}
