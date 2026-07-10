### goal 
Frontend: GitHub Pages ✅

Backend: Oracle Cloud Free VM ✅

Database: MySQL ✅

Source Code: GitHub ✅


## checklist

| Category              | Software / Component              | Why Install / Use?             | Verification Command          | Status                   | Your Version           |
| --------------------- | --------------------------------- | ------------------------------ | ----------------------------- | ------------------------ | ---------------------- |
| Version Control       | Git                               | Source code management         | `git --version`               | ✅ Installed              | 2.40.1.windows.1       |
| Source Code Hosting   | GitHub                            | Store project & collaboration  | `git remote -v`               | ✅ Using                  | Repository Connected   |
| Frontend Hosting      | GitHub Pages                      | Host HTML/CSS/JS website       | Open GitHub Pages URL         | ✅ Deployed               | Running                |
| Programming Language  | Java (JDK)                        | Spring Boot backend            | `java --version`              | ✅ Installed              | *(Run to check)*       |
| Build Tool            | Maven                             | Build Spring Boot project      | `mvn --version`               | ✅ Installed              | *(Run to check)*       |
| Backend Framework     | Spring Boot                       | REST API backend               | `mvn spring-boot:run`         | 🟡 Local Development     | *(Project)*            |
| Database              | MySQL Server                      | Store application data         | `mysql --version`             | ✅ Installed              | *(Run to check)*       |
| Database GUI          | MySQL Workbench                   | Manage MySQL visually          | Open MySQL Workbench          | ✅ Installed              | *(Optional)*           |
| API Testing           | Postman                           | Test REST APIs                 | Open Postman                  | ✅ Optional               | *(Installed if using)* |
| Code Editor           | VS Code                           | Write code                     | `code --version`              | ✅ Installed              | *(Run to check)*       |
| Runtime               | Node.js                           | Frontend tooling               | `node --version`              | ✅ Installed              | v22.19.0               |
| Package Manager       | npm                               | Install JavaScript packages    | `npm --version`               | ✅ Installed              | 10.9.3                 |
| Cloud Provider        | Oracle Cloud Free VM              | Host backend & database        | SSH login (`ssh ubuntu@<ip>`) | ❌ Not Yet                |                        |
| Operating System      | Ubuntu Server                     | Run backend on cloud           | `lsb_release -a`              | ❌ Not Yet                |                        |
| Database (Production) | MySQL (Oracle VM)                 | Online database                | `systemctl status mysql`      | ❌ Not Yet                |                        |
| Reverse Proxy         | Nginx                             | Serve HTTPS & reverse proxy    | `nginx -v`                    | ❌ Later                  |                        |
| SSL Certificate       | Let's Encrypt                     | HTTPS                          | `certbot --version`           | ❌ Later                  |                        |
| Domain (Optional)     | Custom Domain                     | Professional URL               | Open website                  | ❌ Later                  |                        |
| CI/CD                 | GitHub Actions                    | Automatic build/deploy         | GitHub → Actions              | 🟡 Basic Workflow Exists | `backend-ci.yml`       |
| Environment Variables | `.env` / `application.properties` | Store secrets & DB credentials | Check configuration           | 🟡 Local                 |                        |
| Logging               | Spring Boot Logs                  | Debug & monitor application    | `tail -f application.log`     | ❌ Later                  |                        |
| Backup                | MySQL Backup                      | Prevent data loss              | `mysqldump --version`         | ❌ Later                  |                        |
| Monitoring (Optional) | Spring Boot Actuator              | Health & metrics               | `/actuator/health`            | ❌ Later                  |                        |
