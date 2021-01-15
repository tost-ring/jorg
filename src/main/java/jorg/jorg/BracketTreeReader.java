package jorg.jorg;

import suite.suite.Subject;
import suite.suite.Suite;
import suite.suite.action.Action;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

public class BracketTreeReader {

    private ObjectFactory factory;

    public BracketTreeReader() {
        this(new ObjectFactory(StandardInterpreter.getAll()));
    }

    public BracketTreeReader(ObjectFactory factory) {
        this.factory = factory;
    }

    public ObjectFactory getFactory() {
        return factory;
    }

    public void setFactory(ObjectFactory factory) {
        this.factory = factory;
    }

    public BracketTreeReader withRecipe(Class<?> type, Action recipe) {
        factory.setConstructor(type, recipe);
        return this;
    }

    public<T> BracketTreeReader withRecipe(Class<T> type, BiConsumer<Subject, ObjectFactory> recipe) {
        factory.setConstructor(type, recipe);
        return this;
    }

    public BracketTreeReader withParam(String ref, Object o) {
        factory.setParam(ref, o);
        return this;
    }

    public Subject read(String filePath) {
        return loadWell(new File(filePath));
    }

    public Subject read(File file) {
        return loadWell(file);
    }

    public Subject read(InputStream inputStream) {
        return loadWell(inputStream);
    }

    public Subject parse(String jorg) {
        InputStream inputStream = new ByteArrayInputStream(jorg.getBytes());
        return loadWell(inputStream);
    }

    public Subject loadWell(File file) {
        try {
            return load(file);
        } catch (Exception e) {
            e.printStackTrace();
            return Suite.set();
        }
    }

    public Subject load(File file) throws IOException, JorgReadException {
        return load(new FileInputStream(file));
    }

    public Subject loadWell(URL url) {
        try {
            return load(url);
        } catch (Exception e) {
            e.printStackTrace();
            return Suite.set();
        }
    }

    public Subject load(URL url) throws IOException, JorgReadException {
        URLConnection connection = url.openConnection();
        return load(connection.getInputStream());
    }

    public Subject loadWell(InputStream inputStream) {
        try {
            return load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return Suite.set();
        }
    }

    public Subject load(InputStream inputStream) throws JorgReadException {
        BracketTreeProcessor processor = new BracketTreeProcessor();
        processor.ready();
        try (inputStream) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            int code = reader.read();
            while (code != -1) {
                processor.advance(code);
                code = reader.read();
            }
            return factory.load(processor.finish());
        }catch(Exception e) {
            throw new JorgReadException(e);
        }
    }
}
