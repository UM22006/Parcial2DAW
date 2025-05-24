package com.example.application.views.estudiantes;

import com.example.application.data.Estudiante;
import com.example.application.services.EstudianteService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Estudiantes")
@Route(value = "estudiantes", layout = MainLayout.class)
@PermitAll
public class EstudiantesView extends VerticalLayout {

    private final EstudianteService estudianteService;

    private final TextField carnet = new TextField("Carnet");
    private final TextField primerNombre = new TextField("Primer Nombre");
    private final TextField segundoNombre = new TextField("Segundo Nombre");
    private final TextField primerApellido = new TextField("Primer Apellido");
    private final TextField segundoApellido = new TextField("Segundo Apellido");
    private final TextField numeroTelefono = new TextField("Teléfono");
    private final EmailField correo = new EmailField("Correo");
    private final IntegerField edad = new IntegerField("Edad");
    private final TextField direccion = new TextField("Dirección");
    private final TextField carrera = new TextField("Carrera");

    private final Button guardar = new Button("💾 Guardar");
    private final Button limpiar = new Button("🧹 Limpiar");

    private final Grid<Estudiante> grid = new Grid<>(Estudiante.class, false);
    private final BeanValidationBinder<Estudiante> binder = new BeanValidationBinder<>(Estudiante.class);

    private Estudiante estudianteActual;

    public EstudiantesView(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("🎓 Registro de Estudiantes");

        FormLayout formLayout = crearFormulario();
        configurarBotones();
        configurarGrid();
        actualizarGrid();

        HorizontalLayout acciones = new HorizontalLayout(guardar, limpiar);
        acciones.setSpacing(true);
        acciones.setJustifyContentMode(JustifyContentMode.CENTER);
        acciones.setWidthFull();
        acciones.getStyle().set("margin-top", "0.5em");

        add(titulo, formLayout, acciones, grid);
    }

    private FormLayout crearFormulario() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );

        formLayout.add(
            carnet, primerNombre, segundoNombre,
            primerApellido, segundoApellido,
            numeroTelefono, correo, edad,
            direccion, carrera
        );

        formLayout.setColspan(direccion, 2);
        formLayout.setColspan(carrera, 2);

        binder.bindInstanceFields(this);
        return formLayout;
    }

    private void configurarBotones() {
        guardar.setWidth("160px");
        guardar.getStyle()
            .set("background-color", "#2563eb")
            .set("color", "white")
            .set("border-radius", "8px")
            .set("font-size", "14px");

        limpiar.setWidth("160px");
        limpiar.getStyle()
            .set("background-color", "#6b7280")
            .set("color", "white")
            .set("border-radius", "8px")
            .set("font-size", "14px");

        guardar.addClickListener(e -> guardarEstudiante());
        limpiar.addClickListener(e -> limpiarFormulario());
    }

    private void guardarEstudiante() {
        Estudiante nuevo = (estudianteActual != null) ? estudianteActual : new Estudiante();

        if (binder.writeBeanIfValid(nuevo)) {
            estudianteService.save(nuevo);
            Notification.show("✅ Estudiante guardado con éxito", 3000, Notification.Position.MIDDLE);
            estudianteActual = null;
            binder.readBean(null);
            actualizarGrid();
        } else {
            Notification.show("❌ Corrige los errores del formulario", 4000, Notification.Position.MIDDLE);
        }
    }

    private void limpiarFormulario() {
        estudianteActual = null;
        binder.readBean(null);
    }

    private void configurarGrid() {
        grid.removeAllColumns();

        // ✅ Mostrar todos los campos
        grid.addColumn(Estudiante::getCarnet).setHeader("Carnet");
        grid.addColumn(Estudiante::getPrimerNombre).setHeader("Primer Nombre");
        grid.addColumn(Estudiante::getSegundoNombre).setHeader("Segundo Nombre");
        grid.addColumn(Estudiante::getPrimerApellido).setHeader("Primer Apellido");
        grid.addColumn(Estudiante::getSegundoApellido).setHeader("Segundo Apellido");
        grid.addColumn(Estudiante::getNumeroTelefono).setHeader("Teléfono");
        grid.addColumn(Estudiante::getCorreo).setHeader("Correo");
        grid.addColumn(Estudiante::getEdad).setHeader("Edad");
        grid.addColumn(Estudiante::getDireccion).setHeader("Dirección");
        grid.addColumn(Estudiante::getCarrera).setHeader("Carrera");

        // ✅ Botón Editar con estilo
        grid.addComponentColumn(estudiante -> {
            Button editar = new Button("✏️ Editar");
            editar.addClickListener(click -> {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("¿Editar estudiante?");
                dialog.setText("¿Deseas cargar los datos de " + estudiante.getCarnet() + " en el formulario?");
                dialog.setConfirmText("Sí, editar");
                dialog.setCancelText("Cancelar");

                dialog.addConfirmListener(e -> {
                    editarEstudiante(estudiante);
                    Notification.show("✏️ Estudiante cargado para edición", 2500, Notification.Position.BOTTOM_END);
                });

                dialog.open();
            });

            editar.getStyle()
                .set("background-color", "#2563eb")
                .set("color", "white")
                .set("font-size", "13px")
                .set("border-radius", "6px");

            return editar;
        }).setHeader("Editar");

        // ✅ Botón Eliminar con estilo
        grid.addComponentColumn(estudiante -> {
            Button eliminar = new Button("🗑 Eliminar");
            eliminar.addClickListener(click -> {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("¿Eliminar estudiante?");
                dialog.setText("¿Está seguro que desea eliminar al estudiante: " + estudiante.getCarnet() + "?");
                dialog.setConfirmText("Sí, eliminar");
                dialog.setCancelText("Cancelar");

                dialog.addConfirmListener(e -> {
                    estudianteService.delete(estudiante);
                    actualizarGrid();
                    limpiarFormulario();
                    Notification.show("🗑 Estudiante eliminado", 3000, Notification.Position.MIDDLE);
                });

                dialog.open();
            });

            eliminar.getStyle()
                .set("background-color", "#dc2626")
                .set("color", "white")
                .set("font-size", "13px")
                .set("border-radius", "6px");

            return eliminar;
        }).setHeader("Eliminar");

        grid.setAllRowsVisible(true);
        grid.setWidthFull();
        grid.getStyle().set("overflow-x", "auto");
    }

    private void editarEstudiante(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        binder.readBean(estudiante);
    }

    private void actualizarGrid() {
        grid.setItems(estudianteService.findAll());
    }
}
