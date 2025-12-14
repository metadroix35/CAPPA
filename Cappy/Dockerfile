FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 10000
ENV PORT=10000

CMD ["java", "-Dserver.port=10000", "-jar", "app.jar"]
