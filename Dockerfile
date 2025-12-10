# ---------- BUILD STAGE ----------
FROM gradle:8.10-jdk17-alpine AS build
WORKDIR /app

COPY . .
RUN ./gradlew bootJar --no-daemon


# ---------- RUN STAGE ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ENV EXPENSE_FILE_PATH=/app/expenses.json
# Copy the built jar
COPY --from=build /app/build/libs/*.jar app.jar

# Copy your seed data file into the same working directory
COPY --from=build /app/expenses.json expenses.json

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
