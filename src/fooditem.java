public class fooditem {
    String itemname;
    int itemprice;

    fooditem(String itemname,int itemprice){
        this.itemname=itemname;
        this.itemprice=itemprice;
    }

    void display(){
        System.out.printf("%-20s Rs. %d%n", itemname, itemprice);
    }
}

class PreparedFood extends fooditem {
    PreparedFood(String itemname, int itemprice) {
        super(itemname, itemprice);
    }
}

class PackagedFood extends fooditem {
    PackagedFood(String itemname, int itemprice) {
        super(itemname, itemprice);
    }
}

