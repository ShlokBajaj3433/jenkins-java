import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class Main {
    private static final String COMMON_STYLES = """
            <style>
                @import url('https://fonts.googleapis.com/css2?family=Manrope:wght@400;600;700;800&family=Fraunces:opsz,wght@9..144,700&display=swap');
                :root {
                    --bg: #f7f2e7;
                    --surface: #fffdf8;
                    --primary: #0a5c4a;
                    --accent: #efb809;
                    --ink: #1f2a37;
                    --muted: #5f6b7a;
                    --line: #e6dcc8;
                }
                * { box-sizing: border-box; }
                body {
                    margin: 0;
                    font-family: 'Manrope', sans-serif;
                    color: var(--ink);
                    background:
                        radial-gradient(circle at 10% 10%, #fff5d2 0, #f7f2e7 35%),
                        radial-gradient(circle at 90% 15%, #d9efe9 0, #f7f2e7 40%);
                }
                .shell {
                    max-width: 1100px;
                    margin: 0 auto;
                    padding: 1.25rem;
                }
                .nav {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 0.8rem;
                    align-items: center;
                    justify-content: space-between;
                    margin-bottom: 1.2rem;
                    padding: 0.9rem 1rem;
                    border: 1px solid var(--line);
                    background: var(--surface);
                    border-radius: 14px;
                }
                .brand {
                    font-family: 'Fraunces', serif;
                    font-size: 1.25rem;
                    color: var(--primary);
                }
                .links {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 0.55rem;
                }
                .links a {
                    text-decoration: none;
                    color: var(--ink);
                    padding: 0.5rem 0.7rem;
                    border-radius: 8px;
                    border: 1px solid transparent;
                }
                .links a:hover {
                    border-color: var(--line);
                    background: #fff;
                }
                .hero {
                    background: linear-gradient(125deg, #0a5c4a, #128667);
                    color: #fff;
                    border-radius: 18px;
                    padding: 2rem;
                    border: 1px solid #0b7158;
                    box-shadow: 0 20px 40px rgba(5, 49, 38, 0.18);
                    margin-bottom: 1rem;
                }
                .hero h1 {
                    margin: 0 0 0.6rem;
                    font-size: clamp(1.7rem, 4vw, 2.8rem);
                }
                .hero p {
                    margin: 0;
                    color: #def5ee;
                    max-width: 70ch;
                }
                .grid {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                    gap: 0.85rem;
                }
                .card {
                    background: #fff;
                    border: 1px solid var(--line);
                    border-radius: 14px;
                    padding: 1rem;
                    transition: transform .2s ease, box-shadow .2s ease;
                }
                .card:hover {
                    transform: translateY(-3px);
                    box-shadow: 0 10px 20px rgba(24, 39, 75, 0.08);
                }
                .card h3 { margin: 0 0 0.35rem; }
                .muted { color: var(--muted); }
                .price {
                    display: inline-block;
                    margin-top: 0.4rem;
                    background: #fff6da;
                    border: 1px solid #f1dd98;
                    color: #7e5c00;
                    border-radius: 999px;
                    padding: 0.2rem 0.7rem;
                    font-weight: 700;
                }
                .btn {
                    display: inline-block;
                    text-decoration: none;
                    margin-top: 0.7rem;
                    padding: 0.55rem 0.85rem;
                    background: var(--primary);
                    color: #fff;
                    border-radius: 9px;
                    font-weight: 700;
                }
                .btn.alt { background: #1f2a37; }
                .footer {
                    text-align: center;
                    color: var(--muted);
                    margin: 1rem 0 0.2rem;
                    font-size: 0.9rem;
                }
            </style>
            """;

    private static int resolvePort() {
        String portValue = System.getenv("PORT");
        if (portValue == null || portValue.isBlank()) {
            return 8080;
        }
        return Integer.parseInt(portValue);
    }

    private static void writeResponse(HttpExchange exchange, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private static String layout(String title, String body) {
        return """
                <!doctype html>
                <html lang=\"en\">
                <head>
                    <meta charset=\"utf-8\" />
                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />
                    <title>%s</title>
                    %s
                </head>
                <body>
                    <main class=\"shell\">
                        <nav class=\"nav\">
                            <div class=\"brand\">Northstar Commerce</div>
                            <div class=\"links\">
                                <a href=\"/\">Home</a>
                                <a href=\"/catalog\">Catalog</a>
                                <a href=\"/cart\">Cart</a>
                                <a href=\"/checkout\">Checkout</a>
                                <a href=\"/enterprise\">Enterprise</a>
                            </div>
                        </nav>
                        %s
                        <footer class=\"footer\">Java storefront powered by lightweight HttpServer</footer>
                    </main>
                </body>
                </html>
                """.formatted(title, COMMON_STYLES, body);
    }

    private static String homePage() {
        return layout("Northstar Commerce | Home", """
                <section class=\"hero\">
                    <h1>Build fast shopping experiences for modern teams</h1>
                    <p>Northstar Commerce blends B2C checkout speed with enterprise procurement controls, contract pricing, and B2B account workflows.</p>
                    <a class=\"btn\" href=\"/catalog\">Explore Catalog</a>
                    <a class=\"btn alt\" href=\"/enterprise\">View Enterprise Suite</a>
                </section>
                <section class=\"grid\">
                    <article class=\"card\"><h3>Smart Recommendations</h3><p class=\"muted\">AI-driven bundles for higher average order value.</p></article>
                    <article class=\"card\"><h3>Omnichannel Inventory</h3><p class=\"muted\">Unified stock across retail, warehouse, and online.</p></article>
                    <article class=\"card\"><h3>Procurement Controls</h3><p class=\"muted\">Role-based approvals and configurable spend limits.</p></article>
                </section>
                """);
    }

    private static String catalogPage() {
        return layout("Northstar Commerce | Catalog", """
                <section class=\"hero\"><h1>Trending Products</h1><p>Curated catalog blocks with pricing badges and quick actions.</p></section>
                <section class=\"grid\">
                    <article class=\"card\"><h3>Atlas Chair</h3><p class=\"muted\">Ergonomic mesh seating for long workdays.</p><span class=\"price\">$249</span></article>
                    <article class=\"card\"><h3>Pulse Headset</h3><p class=\"muted\">Noise-canceling headset with dual-device pairing.</p><span class=\"price\">$129</span></article>
                    <article class=\"card\"><h3>Nova Monitor</h3><p class=\"muted\">27-inch color-accurate display for design teams.</p><span class=\"price\">$389</span></article>
                    <article class=\"card\"><h3>Forge Desk</h3><p class=\"muted\">Electric standing desk with cable management.</p><span class=\"price\">$599</span></article>
                </section>
                """);
    }

    private static String cartPage() {
        return layout("Northstar Commerce | Cart", """
                <section class=\"hero\"><h1>Your Cart</h1><p>Saved items, volume discounts, and tax preview in one place.</p></section>
                <section class=\"grid\">
                    <article class=\"card\"><h3>Atlas Chair x2</h3><p class=\"muted\">Unit: $249 | Subtotal: $498</p></article>
                    <article class=\"card\"><h3>Pulse Headset x1</h3><p class=\"muted\">Unit: $129 | Subtotal: $129</p></article>
                    <article class=\"card\"><h3>Estimated Total</h3><p class=\"muted\">Items: $627 | Tax: $56.43 | Shipping: $18</p><span class=\"price\">$701.43</span></article>
                </section>
                """);
    }

    private static String checkoutPage() {
        return layout("Northstar Commerce | Checkout", """
                <section class=\"hero\"><h1>Checkout</h1><p>Multi-step payment, shipping, and order review for conversion-ready checkout.</p></section>
                <section class=\"grid\">
                    <article class=\"card\"><h3>Shipping</h3><p class=\"muted\">2-day priority available for all metro regions.</p></article>
                    <article class=\"card\"><h3>Payments</h3><p class=\"muted\">Cards, ACH, invoicing, and purchase orders supported.</p></article>
                    <article class=\"card\"><h3>Security</h3><p class=\"muted\">Tokenized checkout with fraud and chargeback signals.</p></article>
                </section>
                """);
    }

    private static String enterprisePage() {
        return layout("Northstar Commerce | Enterprise", """
                <section class=\"hero\"><h1>Enterprise Commerce Stack</h1><p>Serve global teams with custom catalogs, contract terms, and analytics at scale.</p></section>
                <section class=\"grid\">
                    <article class=\"card\"><h3>Account Hierarchies</h3><p class=\"muted\">Parent-child buyers with independent budgets.</p></article>
                    <article class=\"card\"><h3>Contract Pricing</h3><p class=\"muted\">Negotiated rates auto-applied by account segment.</p></article>
                    <article class=\"card\"><h3>Approval Flows</h3><p class=\"muted\">Configurable order routing for finance compliance.</p></article>
                    <article class=\"card\"><h3>Operational Analytics</h3><p class=\"muted\">Live dashboards for GMV, margin, and fulfillment SLAs.</p></article>
                </section>
                """);
    }

    public static void main(String[] args) throws IOException {
        int port = resolvePort();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", exchange -> writeResponse(exchange, homePage(), "text/html; charset=utf-8"));
        server.createContext("/catalog", exchange -> writeResponse(exchange, catalogPage(), "text/html; charset=utf-8"));
        server.createContext("/cart", exchange -> writeResponse(exchange, cartPage(), "text/html; charset=utf-8"));
        server.createContext("/checkout", exchange -> writeResponse(exchange, checkoutPage(), "text/html; charset=utf-8"));
        server.createContext("/enterprise", exchange -> writeResponse(exchange, enterprisePage(), "text/html; charset=utf-8"));

        server.createContext("/health", exchange -> writeResponse(
                exchange,
                "{\"status\":\"ok\",\"project\":\"java\"}",
                "application/json; charset=utf-8"
        ));

        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("Java web server running on port " + port);
    }
}
