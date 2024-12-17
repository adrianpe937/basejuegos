package org.example.Model;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

public class Juego {

    @BsonId
    private ObjectId id;
    private StringProperty titulo;
    private StringProperty genero;
    private DoubleProperty precio;
    private StringProperty campofechaLanzamiento;


    public Juego(String titulo, String genero, double precio, String campofechaLanzamiento) {
        this.titulo = new SimpleStringProperty(titulo);
        this.genero = new SimpleStringProperty(genero);
        this.precio = new SimpleDoubleProperty(precio);
        this.campofechaLanzamiento = new SimpleStringProperty(campofechaLanzamiento);
    }

    public Juego() {

    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
    // Getters para las propiedades
    public StringProperty tituloProperty() {
        return titulo;
    }

    public StringProperty generoProperty() {
        return genero;
    }

    public DoubleProperty precioProperty() {
        return precio;
    }

    public StringProperty campofechaLanzamientoProperty() {
        return campofechaLanzamiento;
    }


    public String getcampoTitulo() {
        return titulo.get();
    }

    public String getcampoGenero() {
        return genero.get();
    }

    public double getcampoPrecio() {
        return precio.get();
    }

    public String getcampoFechaLanzamiento() {
        return campofechaLanzamiento.get();
    }


    public static void eliminar(String titulo){
        MongoDatabase db = Database.conexion();
        MongoCollection<Document> coleccion = db.getCollection("juegos");

        Document filtro = new Document("titulo", titulo);

        coleccion.deleteOne(filtro);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText("Juego eliminado: " + titulo);
        alert.showAndWait();
    }

    public static ObservableList<Juego> getJuegos() {
        ObservableList<Juego> juegos = FXCollections.observableArrayList();
        try {
            MongoDatabase db = org.example.Model.Database.conexion();
            MongoCollection<Document> coleccion = db.getCollection("juegos");


            try (MongoCursor<Document> cursor = coleccion.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String titulo = doc.getString("titulo");
                    String genero = doc.getString("genero");
                    double precio = doc.getDouble("precio");
                    String campofechaLanzamiento = doc.getString("fecha_lanzamiento");


                    juegos.add(new Juego(titulo, genero, precio, campofechaLanzamiento));
                }
            }
        } catch (Exception e) {
            System.out.println("Error al obtener juegos: " + e.getMessage());
        }

        return juegos;
    }

    public static void crear(String titulo, String genero, Double precio, String campofechaLanzamiento){
        try {
            MongoDatabase db = Database.conexion();
            MongoCollection<Document> coleccion = db.getCollection("juegos");


            Document juego = new Document("titulo", titulo)
                    .append("genero", genero)
                    .append("precio", precio)
                    .append("fecha_lanzamiento", campofechaLanzamiento);


            coleccion.insertOne(juego);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("Juego creado correctamente  Nombre: " + titulo);
            alert.showAndWait();

        }catch(Exception e) {
            System.out.println("Error al insertar el juego: " + e.getMessage());
        }
    }

    public static void editar(String tituloAntiguo, String titulo, String genero, Double precio, String campofechaLanzamiento) {
        try {

            MongoDatabase db = Database.conexion();
            MongoCollection<Document> coleccion = db.getCollection("juegos");


            Bson filtro = Filters.eq("titulo", tituloAntiguo);


            coleccion.updateOne(filtro, Updates.combine(
                    Updates.set("titulo", titulo),
                    Updates.set("genero", genero),
                    Updates.set("precio", precio),
                    Updates.set("fechaLanzamiento", campofechaLanzamiento)
            ));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("Juego actualizado exitosamente");
            alert.showAndWait();


        } catch (Exception e) {
            System.out.println("Error al editar el juego: " + e.getMessage());
        }
    }

    public static void eliminarTodo(String genero){
        MongoDatabase db = Database.conexion();
        MongoCollection<Document> coleccion = db.getCollection("juegos");

        Document filtro = new Document("genero", genero);

        coleccion.deleteMany(filtro);

    }

    public static boolean comparar(String titulo) {
        try {
            MongoDatabase db = Database.conexion();
            MongoCollection<Document> coleccion = db.getCollection("juegos");

            Document filtro = new Document("titulo", titulo);

            long count = coleccion.countDocuments(filtro);

            return count == 0;

        } catch (Exception e) {
            System.out.println("Error al comparar el titulo: " + e.getMessage());
            return false;
        }
    }

    public static ObservableList<String> obtenerGeneros() {
        ObservableList<String> generos = FXCollections.observableArrayList();

        try {

            MongoDatabase db = Database.conexion();
            MongoCollection<Document> coleccion = db.getCollection("juegos");


            FindIterable<Document> documentos = coleccion.find();


            for (Document doc : documentos) {
                String genero = doc.getString("genero");


                if (genero != null && !genero.trim().isEmpty() && !generos.contains(genero)) {
                    generos.add(genero);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al obtener géneros: " + e.getMessage());
        }

        return generos;
    }
}