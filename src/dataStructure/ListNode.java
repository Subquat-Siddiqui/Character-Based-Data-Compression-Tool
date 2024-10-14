package dataStructure;

/**
 * @author Subquat Siddiqui
 * @since July 1st, 2024
 * This class represents a node in a linked list, where each node
 * contains the root of a tree
 */
public class ListNode {
    public TreeNode tree;
    public ListNode next;

    public ListNode(TreeNode tree, ListNode next){
        this.tree = tree;
        this.next = next;
    }


}
