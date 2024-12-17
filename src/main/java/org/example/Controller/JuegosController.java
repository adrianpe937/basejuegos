package org.example.Controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.Model.Juego;

import java.net.URL;
import java.util.ResourceBundle;

public class JuegosController implements Initializable {

    @FXML
    private TableView<Juego> tablaJuegos;
    @FXML
    private TableColumn<Juego, String> columnaTitulo;
    @FXML
    private TableColumn<Juego, String> columnaGenero;
    @FXML
    private TableColumn<Juego, Double> columnaPrecio;
    @FXML
    private TableColumn<Juego, String> columnaFechaLanzamiento;
    @FXML
    private TextField campoTitulo;
    @FXML
    private TextField campoGenero;
    @FXML
    private TextField campoPrecio;
    @FXML
    private TextField campoFechaLanzamiento;
    @FXML
    private Button botoncrear;
    @FXML
    private Button botoneditar;
    @FXML
    private Button botoneliminar;
    @FXML
    private Button botoneliminarContenido;
    @FXML
    private Button botoneliminarGenero;
    @FXML
    private ComboBox<String> Combogenero;

    private Juego juegoSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        desactivarBotones();

        botoncrear.setOnMouseClicked(event -> crear());
        botoneditar.setOnMouseClicked(event -> editar());
        botoneliminar.setOnMouseClicked(event -> eliminar());
        botoneliminarGenero.setOnMouseClicked(event -> eliminarTodo());
        botoneliminarContenido.setOnMouseClicked(event -> limpiarCampos());

        ObservableList<String> generos = Juego.obtenerGeneros();

        Combogenero.setItems(generos);


        columnaTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        columnaGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        columnaPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        columnaFechaLanzamiento.setCellValueFactory(new PropertyValueFactory<>("campoFechaLanzamiento"));

        cargarJuegos();


        tablaJuegos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                juegoSeleccionado = newValue;
                campoTitulo.setText(newValue.getcampoTitulo());
                campoGenero.setText(newValue.getcampoGenero());
                campoPrecio.setText(String.valueOf(newValue.getcampoPrecio()));
                campoFechaLanzamiento.setText(newValue.getcampoFechaLanzamiento());

                // Activar los botones "Editar", "Eliminar" cuando se selecciona un juego
                activarBotonesSeleccionado();
            } else {
                // Si no hay selección, desactivar los botones
                desactivarBotones();
            }
        });

        // Listener para actualizar botones según el texto en los campos
        campoTitulo.textProperty().addListener((observable, oldValue, newValue) -> actualizarBotones());
        campoGenero.textProperty().addListener((observable, oldValue, newValue) -> actualizarBotones());
        campoPrecio.textProperty().addListener((observable, oldValue, newValue) -> actualizarBotones());
        campoFechaLanzamiento.textProperty().addListener((observable, oldValue, newValue) -> actualizarBotones());

        // Listener para el ComboBox de géneros
        Combogenero.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                botoneliminarContenido.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-weight: bold;");
            }
        });
    }


    public void cargarJuegos() {
        ObservableList<Juego> juegosData = Juego.getJuegos();  // Obtener todos los juegos desde la BD
        tablaJuegos.setItems(juegosData);  // Establecer los items de la TableView con la lista de juegos
    }


    public void crear() {
        if (!campoTitulo.getText().trim().isEmpty()) {
            String titulo = campoTitulo.getText();
            if (Juego.comparar(titulo)) {
                Double precio = 0.0;

                String genero = campoGenero.getText();
                if (!campoPrecio.getText().trim().isEmpty()) {
                    precio = Double.parseDouble(campoPrecio.getText());
                }
                String fecha = campoFechaLanzamiento.getText();

                Juego.crear(titulo, genero, precio, fecha);

                cargarJuegos();
                ObservableList<String> generos = Juego.obtenerGeneros();
                Combogenero.setItems(generos);
            } else {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Información");
                alert.setHeaderText(null);
                alert.setContentText("Ya existe un juego con este título");
                alert.showAndWait();
            }
        } else {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("El campo 'Título' es obligatorio");
            alert.showAndWait();
        }
    }


    public void editar() {
        if (juegoSeleccionado != null) {
            String titulo = juegoSeleccionado.getcampoTitulo();
            String nuevoTitulo = campoTitulo.getText();
            String nuevoGenero = campoGenero.getText();
            Double nuevoPrecio = Double.parseDouble(campoPrecio.getText());
            String nuevaFecha = campoFechaLanzamiento.getText();

            Juego.editar(titulo, nuevoTitulo, nuevoGenero, nuevoPrecio, nuevaFecha);

            cargarJuegos();
            ObservableList<String> generos = Juego.obtenerGeneros();
            Combogenero.setItems(generos);

            limpiarCampos();
        } else {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona un juego para editar");
            alert.showAndWait();
        }
    }


    public void eliminar() {
        if (juegoSeleccionado != null) {
            String titulo = juegoSeleccionado.getcampoTitulo();
            Juego.eliminar(titulo);
            cargarJuegos();
        }else {
            // Mostrar alerta si no hay un género seleccionado
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("Selecciona un juego para eliminar");
            alert.showAndWait();
        }
    }


    public void eliminarTodo() {
        if (Combogenero.getValue() != null) {
            String genero = Combogenero.getValue();
            Juego.eliminarTodo(genero);
            cargarJuegos();
        } else {
            // Mostrar alerta si no hay un género seleccionado
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText("No hay ningún género seleccionado");
            alert.showAndWait();
        }
    }


    public void limpiarCampos() {
        campoTitulo.clear();
        campoGenero.clear();
        campoPrecio.clear();
        campoFechaLanzamiento.clear();
    }

    // Actualizar el estado de los botones
    public void actualizarBotones() {
        if (campoTitulo.getText().trim().isEmpty() && campoGenero.getText().trim().isEmpty() && campoPrecio.getText().trim().isEmpty() && campoFechaLanzamiento.getText().trim().isEmpty()) {
            desactivarBotones();  // Si los campos están vacíos, poner los botones en gris
        } else {
            activarBotonesTexto();  // Si hay texto, activar "Eliminar", "Eliminar contenido" y "Crear"
        }
    }

    private void activarBotonesTexto() {
        botoncrear.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");
        botoneliminarContenido.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-font-weight: bold;");
    }


    private void activarBotonesSeleccionado() {
        botoneditar.setStyle("-fx-background-color: #f7a900; -fx-text-fill: white; -fx-font-weight: bold;");
        botoneliminar.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        botoneliminarContenido.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-font-weight: bold;");
    }


    private void desactivarBotones() {
        botoncrear.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
        botoneditar.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
        botoneliminar.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
        botoneliminarContenido.setStyle("-fx-background-color: gray; -fx-text-fill: white;");
    }

}
