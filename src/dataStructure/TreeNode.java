package dataStructure;

/**
 * @author Subquat Siddiqui
 * @since July 1st, 2024
 * This class represents a tree node, where each node contains a String
 * and the frequency of that String, alongside pointers to the left and right child nodes.
 */
public class TreeNode implements Comparable<TreeNode>{
    public TreeNode left;
    public String text;
    public int frequency;
    public TreeNode right;

    public TreeNode(TreeNode left, String text, int frequency, TreeNode right){
        this.left = left;
        this.text = text;
        this.frequency = frequency;
        this.right = right;
    }

    @Override
    public int compareTo(TreeNode other) {
        //other TreeNode being passed has higher frequency, meaning it comes after in list
        if(this.frequency > other.frequency) return -1;

        //other TreeNode being passed has lower frequency, meaning it comes before in list
        else if(this.frequency < other.frequency) return 1;

        //both TreeNode's have the same frequency
        else return 0;

    }
}
