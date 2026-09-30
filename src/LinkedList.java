public class LinkedList<T> {
    private Node<T> head ; 
   private Node<T> current;
 public LinkedList () {
    head = current = null ;
 }
 public boolean empty() {
    return head==null ;
 }

 public boolean last() {
    return current.next==null ;
 }
 public boolean full(){
   return false;
 }
public void findFirst(){
 current=head ;   
}
public void findNext(){
    current = current.next ;
}

public T retrieve(){
    return current.data;
}
public void update(T val){

current.data=val;
}

}
