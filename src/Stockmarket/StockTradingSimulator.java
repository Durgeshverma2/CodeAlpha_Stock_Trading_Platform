package Stockmarket;
import java.io.*;
import java.util.*;

class Stock implements Serializable {
    String symbol;
    String name;
    double price;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public void updatePrice() {
        double changePercent = (Math.random() - 0.5) * 0.1; // -5% to +5%
        price = Math.max(1, Math.round((price * (1 + changePercent)) * 100.0) / 100.0);
    }
}

class StockMarket {
    Map<String, Stock> stocks = new HashMap<>();

    public StockMarket() {
        stocks.put("SBI", new Stock("SBI", "State Bank Of India.", 770));
        stocks.put("ICICI", new Stock("ICICI", "ICICI Bank Limited.", 1450.0));
        stocks.put("HDFC", new Stock("HDFC", "HDFC Bank Limited.", 2000.0));
        stocks.put("BSE", new Stock("BSE", "Bombay Stock Exchange.", 2400.0));
        stocks.put("PC", new Stock("PC", "PC Jwelers.", 16.0));
        stocks.put("BHL",new Stock("BHL" ,"Bharat Electronics.",400.0));
        stocks.put("CDSL",new Stock("CDSL" ,"Central Depository Services(India) Limited.",1700.0));
        stocks.put("PVR",new Stock("PVR" ,"PVR Inox.",1000.0));
        stocks.put("RMD",new Stock("RMD" ,"Raymond.",640.0));
        stocks.put("CNB",new Stock("CNB" ,"Canara Bank.",110.0));
        stocks.put("SWG",new Stock("SWG" ,"Swiggy.",400.0));
        stocks.put("TRL",new Stock("TR" ,"Trent.",5400.0));
        stocks.put("IOCL",new Stock("IOCL" ,"Indian Oil Corporation.",145.0));
        
        
        
        
    }

    public void displayMarket() {
        System.out.printf("%-8s %-20s %-10s\n", "Symbol", "Name", "Price(₹)");
        for (Stock stock : stocks.values()) {
            System.out.printf("%-8s %-20s %-10.2f\n", stock.symbol, stock.name, stock.price);
        }
    }

    public void updateMarket() {
        for (Stock stock : stocks.values()) {
            stock.updatePrice();
        }
    }

    public Stock getStock(String symbol) {
        return stocks.get(symbol);
    }
}

class Transaction implements Serializable {
    String stockSymbol;
    int quantity;
    double price;
    String type; // buy/sell
    Date date;

    public Transaction(String symbol, int qty, double price, String type, Date date) {
        this.stockSymbol = symbol;
        this.quantity = qty;
        this.price = price;
        this.type = type;
        this.date = date;
    }
}

class Portfolio implements Serializable {
    double cash;
    Map<String, Integer> holdings = new HashMap<>();
    List<Transaction> transactions = new ArrayList<>();

    public Portfolio(double initialCash) {
        this.cash = initialCash;
    }

    public boolean buy(Stock stock, int qty) {
        double cost = stock.price * qty;
        if (cost > cash) {
            System.out.println("Insufficient Balance.");
            return false;
        }
        cash -= cost;
        holdings.put(stock.symbol, holdings.getOrDefault(stock.symbol, 0) + qty);
        transactions.add(new Transaction(stock.symbol, qty, stock.price, "buy", new Date()));
        System.out.printf("Bought %d shares of %s at ₹%.2f\n", qty, stock.symbol, stock.price);
        return true;
    }

    public boolean sell(Stock stock, int qty) {
        if (holdings.getOrDefault(stock.symbol, 0) < qty) {
            System.out.println("Insufficient shares.");
            return false;
        }
        holdings.put(stock.symbol, holdings.get(stock.symbol) - qty);
        cash += stock.price * qty;
        transactions.add(new Transaction(stock.symbol, qty, stock.price, "sell", new Date()));
        System.out.printf("Sold %d shares of %s at ₹%.2f\n", qty, stock.symbol, stock.price);
        return true;
    }

    public void display(StockMarket market) {
        System.out.println("Portfolio:");
        System.out.printf("Cash Balance: ₹%.2f\n", cash);
        System.out.printf("%-8s %-8s %-12s %-10s\n", "Symbol", "Shares", "CurPrice(₹)", "Value(₹)");
        double total = cash;
        for (String sym : holdings.keySet()) {
            int qty = holdings.get(sym);
            double curPrice = market.getStock(sym).price;
            double val = qty * curPrice;
            total += val;
            System.out.printf("%-8s %-8d %-12.2f %-10.2f\n", sym, qty, curPrice, val);
        }
        System.out.printf("Total Portfolio Value: ₹%.2f\n", total);
    }

    public void displayTransactions() {
        System.out.println("Transaction History:");
        for (Transaction t : transactions) {
            System.out.printf("%tF %<tT - %s %d %s at ₹%.2f\n", t.date, t.type.toUpperCase(), t.quantity, t.stockSymbol, t.price);
        }
    }

    public void save(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        }
    }

    public static Portfolio load(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (Portfolio) in.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}

public class StockTradingSimulator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StockMarket stockMarket = new StockMarket();
        String portfolioFile = "portfolio.ser";
        Portfolio portfolio = Portfolio.load(portfolioFile);
        if (portfolio == null) {
            portfolio = new Portfolio(10000.0);
        }
        System.out.println("Welcome to Stock Trading Simulator!");

        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Display market data");
            System.out.println("2. Buy stock");
            System.out.println("3. Sell stock");
            System.out.println("4. View portfolio");
            System.out.println("5. View transaction history");
            System.out.println("6. Update market prices");
            System.out.println("7. Save & exit");
            System.out.println("8. Spasfic Share price uptate");
            System.out.print("Choose an option: ");
            String opt = sc.nextLine();
            switch (opt) {
                case "1":
                	stockMarket.displayMarket();
                    break;
                case "2":
                    System.out.print("Stock symbol to buy: ");
                    String buySym = sc.nextLine().toUpperCase();
                    Stock buyStock = stockMarket.getStock(buySym);
                    if (buyStock == null) {
                        System.out.println("Invalid symbol.");
                        break;
                    }
                    System.out.print("Quantity: ");
                    int buyQty = Integer.parseInt(sc.nextLine());
                    portfolio.buy(buyStock, buyQty);
                    break;
                case "3":
                    System.out.print("Stock symbol to sell: ");
                    String sellSym = sc.nextLine().toUpperCase();
                    Stock sellStock = stockMarket.getStock(sellSym);
                    if (sellStock == null) {
                        System.out.println("Invalid symbol.");
                        break;
                    }
                    System.out.print("Quantity: ");
                    int sellQty = Integer.parseInt(sc.nextLine());
                    portfolio.sell(sellStock, sellQty);
                    break;
                case "4":
                    portfolio.display(stockMarket);
                    break;
                case "5":
                    portfolio.displayTransactions();
                    break;
                case "6":
                	stockMarket.updateMarket();
                    System.out.println("Market prices updated.");
                    break;
                case "7":
                    try {
                        portfolio.save(portfolioFile);
                        System.out.println("Portfolio saved. Goodbye!");
                    } catch (IOException e) {
                        System.out.println("Failed to save portfolio.");
                    }
                    
                case "8":
                	System.out.println("enter a share symbol");
                	String updateMarketSym = sc.nextLine().toUpperCase();
                	Stock updatePrice= stockMarket.getStock(updateMarketSym);
                	updatePrice.updatePrice();
                  	System.out.println("Stock prices updated.");
                	stockMarket.displayMarket();
                  	break;
			default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
