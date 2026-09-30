public class LinkedList {
    Node<T> head ; 
    Node<T> current;
 public LinkedList () {
    head = current = null ;
 }
 public boolean empty() {
    return head==null ;
 }

 public boolean last() {
    return current.next==null ;
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
