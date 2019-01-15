package GraphDB;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.neo4j.driver.v1.*;

import static org.neo4j.driver.v1.Values.parameters;

public class TestHelloWorld implements AutoCloseable {

    private final Driver driver;

    public TestHelloWorld( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public void printGreeting( final String message )
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "CREATE (a:Greeting) " +
                                    "SET a.message = $message " +
                                    "RETURN a.message + ', from node ' + id(a)",
                            parameters( "message", message ) );
                    return result.single().get( 0 ).asString();

                }
            } );
            System.out.println( greeting );
        }
    }

    private void addPerson(String name){
        try ( Session session = driver.session()){
            try ( Transaction tx = session.beginTransaction()) {
                tx.run("MERGE (a:Person {name:{x}})", parameters("x",name));
                tx.success();
            }
        }
    }

    public void addRelation(String name_1, String relation, String name_2){
        try( Session session = driver.session()){
            try ( Transaction tx = session.beginTransaction()){
                tx.run("MERGE (a:Person {name:{p1}})" +
                          "MERGE (b:Person {name:{p2}})" +
                          "MERGE (a)-[r:" + relation + "]->(b)",parameters("p1",name_1,"p2",name_2,"rel",relation));
                tx.success();
            }
        }
    }

    private void printPeople(String initial){
        try (Session session = driver.session()){
            StatementResult result = session.run(
                    "MATCH (a:Person) WHERE a.name STARTS WITH {x} RETURN a.name AS name",
                    parameters("x",initial));

            while (result.hasNext()){
                Record record = result.next();
                System.out.println(record.get("name").asString());
            }
        }
    }

    public static void main( String... args ) throws Exception
    {
        try ( TestHelloWorld greeter = new TestHelloWorld( "bolt://localhost:7687", "neo4j", "9458ilsj" ) )
        {
            //greeter.printGreeting( "hello, me" );
            //greeter.addPerson("xxx");
            greeter.addRelation("他","她","同学");
            //greeter.printPeople("A");
        }
    }
}