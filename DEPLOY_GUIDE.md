# EV Charging App — Free Deploy Guide (Render.com)

## Maine kya fix kiya hai (before deploying)

1. **`DBConnection.java`** — pehle DB URL hardcoded tha (`localhost:5432`, user `postgres`, pass `root`). Ab ye `DATABASE_URL` environment variable se database details uthata hai. Render free Postgres khud `DATABASE_URL` deta hai, isliye code me kuch manually change nahi karna padega.
2. **`web.xml`** — isme bug tha: saare `<servlet>` tags `<web-app>` root element ke bahar the, aur asli `<web-app>` block ek XML comment ke andar chhupa hua tha. Ye invalid XML hai — Tomcat isko deploy hi nahi karta. Maine isko fix kiya: servlets already apne `@WebServlet(...)` annotations se register ho rahe hain (jo `.java` files me hai), isliye duplicate `<servlet>` entries hata ke sirf CORS filter wala valid `web-app` rakha.
3. **`Dockerfile`** — naya add kiya, jo source code se fresh WAR banata hai aur Tomcat 10 (jakarta.* APIs match karta hai) pe run karta hai, `/ev_charging` path pe — isse frontend ka `app.js` jo `/ev_charging/api/...` call karta hai, bina kisi change ke chalega.
4. **`render.yaml`** — Render blueprint, jisse web app + free Postgres database dono ek click me deploy ho jaate hain.
5. **`schema.sql`** — tumhare DAO code se reverse-engineer kiya gaya database schema (tables: discom, charging_station, "User", vehicle, connector, charging_session, maintenance_ticket, payment). Isse Render ke naye empty Postgres database me run karna hoga.

## Deploy steps (Render.com — 100% free tier)

### Step 1: Code GitHub pe daalo
1. github.com pe naya **public ya private repo** banao (e.g. `ev-charging-app`)
2. Yeh poora `ev_charging` folder (jo maine fix kiya) upload karo:
   - GitHub website pe directly "Add file → Upload files" se sab files drag-drop kar sakte ho
   - Ya `git init`, `git add .`, `git commit`, `git push` se (agar git pata hai)

### Step 2: Render account banao
1. [render.com](https://render.com) pe jao, GitHub se sign up karo (free, no credit card)

### Step 3: Blueprint deploy karo
1. Render dashboard me **"New +" → "Blueprint"**
2. Apna GitHub repo connect karo (jisme `render.yaml` hai)
3. Render khud detect kar lega: 1 web service + 1 Postgres database
4. **"Apply"** click karo — ye dono cheezen free tier pe spin up kar dega
5. 5-10 minute lagega (Docker image build ho rahi hogi)

### Step 4: Database schema load karo
1. Render dashboard me apne Postgres database pe jao
2. **"Connect" → "External Connection"** se connection details copy karo, ya **"Shell"** tab use karo (browser-based psql)
3. `schema.sql` ka content paste karo aur run karo (ye tables bana dega)

### Step 5: App check karo
1. Web service ka URL khulega kuch is type ka: `https://ev-charging-app.onrender.com`
2. Isi URL pe jaake check karo, automatically `/ev_charging/` pe redirect ho jaayega

## Important free-tier baatein
- Render free web service **15 minute inactivity ke baad spin down** ho jata hai, aur next request pe firse start hone me ~30-50 second lagta hai (cold start) — ye normal hai, free tier ki limitation hai.
- Render free Postgres **90 din baad expire** ho jata hai — uske baad naya database banana padega ya paid plan lena padega. Side project/demo ke liye ye theek hai.
- Agar `DB_USER`/`DB_PASS` alag se set karna ho (DATABASE_URL ke bina), to environment variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASS` set kar sakte ho — code dono tarike support karta hai.

## Apne computer pe local test karne ke liye
DB env variables set na karo to ye automatically `localhost:5432/ev_charging_network`, user `postgres`, pass `root` use karega (jaisa pehle tha) — local Postgres installed hona chahiye.
