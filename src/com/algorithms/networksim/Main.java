package com.algorithms.networksim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main {

    public static void main(String[] args) throws IncorrectNetworkPath {

        //Here we can simulate our network
        Network network = new Network();

        network.add("/family/mom", "This is Mom's homepage!");
        network.add("/family/ryan", "This is Ryan's homepage!");
        network.add("/family/dad", "This is Dad's homepage!");
        network.add("/parents/dad", "This is Dad's parent homepage!");

        System.out.println(network.get("/test"));
    }

}




/*
Here network creates our network for us so it is nice and easy and simple
 */
class Network {
    private Node root = new Node();

    public void add(String path, String content) {
        root.PUT(path, content);
    }
    public List<String> get(String path) throws IncorrectNetworkPath {
        return root.GET(path);
    }
}


/*
Here is our node class, this simulates one step towards an endpoint
 */
class Node {
    private String content = "~This endpoint has no content~";
    private HashMap<String, Node> children = new HashMap<>();

    //This returns the path minus the first /.. ie if given the path /home/books it will return /books
    private String cdrPath(String path) {
        String remaining = "";
        String[] s = path.split("/");
        for (int i=2; i<s.length; i++) {
            remaining += "/" + s[i];
        }
        return remaining;
    }
    //The first endpoint in a path ie if given the path /home/books it will return /home
    private String carPath(String path) {
        String remaining = "";
        String[] s = path.split("/");
        return (s.length>1) ? s[1] : "";
    }

    //This recursively calls PUT on itself until its reached the end of the path and then places the content
    protected void PUT(String path, String content) {
        String cdr = cdrPath(path); //The remaining full url
        String car = carPath(path); //The next child we are looking for
        if (car.equals("")) { //This is the node we were trying to create
            this.content = content;
            return;
        }

        if (children.containsKey(car)) { //If the node for this path does not exist, create it!
            children.get(car).PUT(cdr, content);
        } else {
            Node child = new Node();
            children.put(car, child);
            child.PUT(cdr, content);
        }
    }

    //Recursively calls GET on itself, and if the path can not be followed down (ie no children) it throws a custom exception
    protected List<String> GET(String path) throws IncorrectNetworkPath {
        List<String> result = new ArrayList<>();
        String cdr = cdrPath(path); //The remaining full url
        String car = carPath(path); //The next child we are looking for

        if (car.equals("")) { //This is the node we are looking for
            result.add(content);
            return result;
        }


        //Here we can add support for extra characters (like regex)
        switch (car) {
            case "*":
                children.forEach((key, value) -> {
                    try {
                        result.addAll(value.GET(cdr));
                    } catch (IncorrectNetworkPath incorrectNetworkPath) {}
                });
                break;



            //Add any other extra fun symbols you want as a case in here
            default:
                if (children.containsKey(car)) {
                    result.addAll(children.get(car).GET(cdr));
                } else {
                    throw new IncorrectNetworkPath("Endpoint '/" + car + "' Not Found");
                }
                break;
        }
        return result;
    }
}

//Here we just defined a custom exception to be thrown if given a path that does not exist
class IncorrectNetworkPath extends Exception {
    public IncorrectNetworkPath(String errorMessage) {
        super(errorMessage);
    }
}

