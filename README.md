# test

    http://localhost:8080/test-application/swagger-ui/index.html
    docker run --rm --name test-app-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=test_app_db -d -p 5432:5432 postgres:14.2 -c max_locks_per_transaction=128