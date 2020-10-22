import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

class Item implements Comparable<Item> {

    private int id;
    private String menuName;
    private int parentId;
    private boolean isHidden;

    public Item(int id, String menuName, int parentId, boolean isHidden) {
        this.id = id;
        this.menuName = menuName;
        this.parentId = parentId;
        this.isHidden = isHidden;
    }

    public Item(int id, String menuName, boolean isHidden) {
        this.id = id;
        this.menuName = menuName;
        this.isHidden = isHidden;
    }

    public int getId() {
        return id;
    }

    public String getMenuName() {
        return menuName;
    }

    public int getParentId() {
        return parentId;
    }

    public boolean isHidden() {
        return isHidden;
    }

    @Override
    public String toString() {

        if(!isHidden) {
            return menuName;
        }
        return "";
    }

    @Override
    public int compareTo(Item otherItem) {
        return this.menuName.compareTo(otherItem.menuName);
    }
}

class SLLNode<P extends Comparable>{

    SLLNode<P> parent, sibling, firstChild; // Holds the links to the needed nodes

    P element;  // Hold the data

    public SLLNode(P o ) {
        element = o;
        parent = sibling = firstChild = null;
    }

    public P getElement() {
        return element;
    }

    public boolean compareNodes(SLLNode<P> otherNode){
        return this.element.compareTo(otherNode.element) < 0;
    }

}

class SLLTree<E extends Comparable>{

    private SLLNode<E> root;

    public SLLTree() {
        root = null;
    }

    public SLLNode<E> root() {
        return root;
    }

    public void makeRoot(E elem) {
        root = new SLLNode<>(elem);
    }

    public SLLNode<E> parent(SLLNode<E> node) {
        return node.parent;
    }

    public SLLNode<E> addChild(SLLNode<E> node, E elem, boolean isHidden) {
        SLLNode<E> tmp = new SLLNode<>(elem);
        SLLNode<E> header = node.firstChild;
        if(node.firstChild == null || tmp.compareNodes(node.firstChild)){
            tmp.sibling = node.firstChild;
            node.firstChild = tmp;
            tmp.parent = node;
        }
        else{
            while (header != null){
                if(header.sibling == null || tmp.compareNodes(header.sibling)){
                    tmp.sibling = header.sibling;
                    header.sibling = tmp;
                    tmp.parent = node;
                    break;
                }
                header = header.sibling;
            }
        }

        return tmp;
    }

}

public class TestTask {

    private SLLTree<Item> tree;
    private ArrayList<Item> itemList;

    public TestTask(){
        this.itemList = new ArrayList<>();
        this.tree = new SLLTree<>();
        tree.makeRoot(new Item(0,"", true));
    }

    public void printTree() {
        printTreeRecursive(tree.root(), 0);
    }

    public void printTreeRecursive(SLLNode<Item> node, int level){
        if (node == null)
            return;
        int i;
        SLLNode<Item> tmp;

        for (i = 0; i < level && !node.element.isHidden(); i++)
            System.out.print("...");
        System.out.println(node.getElement().toString());
        tmp = node.firstChild;
        while (tmp != null) {
            printTreeRecursive(tmp, level + 1);
            tmp = tmp.sibling;
        }
    }

    public void findNodeById(SLLNode<Item> node, int id, Item item){
        if(node.element.getId() == id)
            this.tree.addChild(node, item, item.isHidden());
        else {
            SLLNode<Item> tmp = node.firstChild;
            while (tmp != null) {
                findNodeById(tmp, id, item);
                tmp = tmp.sibling;
            }
        }
    }

    public void addItemInTree(Item item){
        this.findNodeById(tree.root(), item.getParentId(), item);
    }

    public Item createItemFromArrayOfStrings(String [] array){
        int id = Integer.parseInt(array[0]);
        String menuName = array[1];
        int parentId = 0;
        boolean isHidden = Boolean.parseBoolean(array[3].toLowerCase());

        if(!array[2].equals("NULL"))
            parentId = Integer.parseInt(array[2]);

        return new Item(id, menuName, parentId, isHidden);
    }

    public void readFromFile(String path) throws IOException {
        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(path));

            // Skip the first line
            input.readLine();

            // Create stream of strings from file each line
            // Create Item object form each line
            itemList = input.lines()
                    .map(s -> s.split(";"))
                    .map(this::createItemFromArrayOfStrings)
                    .collect(Collectors.toCollection(ArrayList::new));

            itemList.sort(Comparator.comparingInt(Item::getParentId));
            itemList.forEach(this::addItemInTree);
        }
        finally {
            if(input != null)
                input.close();
        }

    }

    public static void main(String[] args) throws IOException {

        TestTask ts = new TestTask();

        ts.readFromFile("./src/Navigation.csv");
        ts.printTree();
    }
}
