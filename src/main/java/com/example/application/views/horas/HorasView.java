package com.example.application.views.horas;

import com.example.application.data.ControlHoras;
import com.example.application.data.Estudiante;
import com.example.application.services.ControlHorasService;
import com.example.application.services.EstudianteService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.example.application.views.MainLayout;
import org.springframework.security.access.annotation.Secured;
import jakarta.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@PageTitle("Control de Horas")
@Route(value = "horas", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "USER"})
public class HorasView extends VerticalLayout {

    private final ControlHorasService horasService;
    private final EstudianteService estudianteService;

    private final Grid<ControlHoras> grid = new Grid<>(ControlHoras.class, false);
    private final ComboBox<Estudiante> estudianteCombo = new ComboBox<>("Estudiante");
    private final DateTimePicker ingreso = new DateTimePicker("Ingreso");
    private final DateTimePicker salida = new DateTimePicker("Salida");
    private final NumberField horasTrabajadas = new NumberField("Horas trabajadas");
    private final TextArea actividades = new TextArea("Actividades realizadas");

    private final Button guardar = new Button("💾 Guardar");
    private final Button limpiar = new Button("🧹 Limpiar");

    private ControlHoras registroActual = null;

    public HorasView(@Autowired ControlHorasService horasService,
                     @Autowired EstudianteService estudianteService) {
        this.horasService = horasService;
        this.estudianteService = estudianteService;

        configurarFormulario();
        configurarGrid();
        configurarBotones();

        add(formulario(), botones(), grid);
        actualizarGrid();
    }

    private void configurarGrid() {
        grid.removeAllColumns();

        grid.addColumn(control -> control.getEstudiante().getCarnet()).setHeader("Carnet");
        grid.addColumn(ControlHoras::getFechaHoraIngreso).setHeader("Ingreso");
        grid.addColumn(ControlHoras::getFechaHoraSalida).setHeader("Salida");
        grid.addColumn(control -> String.format("%.2f", control.getHorasTrabajadas())).setHeader("Horas");
        grid.addColumn(ControlHoras::getActividadesRealizadas).setHeader("Actividades");

        grid.addComponentColumn(registro -> {
            Button editar = new Button("✏️ Editar");
            editar.addClickListener(e -> {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("¿Editar registro?");
                dialog.setText("¿Deseas cargar este registro en el formulario?");
                dialog.setConfirmText("Sí, editar");
                dialog.setCancelText("Cancelar");

                dialog.addConfirmListener(ev -> {
                    cargarFormulario(registro);
                    Notification.show("✏️ Registro cargado para edición", 2500, Notification.Position.BOTTOM_END);
                });

                dialog.open();
            });

            editar.getStyle()
                .set("background-color", "#2563eb")
                .set("color", "white")
                .set("font-size", "13px")
                .set("border-radius", "6px");

            Button eliminar = new Button("🗑 Eliminar", e -> {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("¿Eliminar registro?");
                dialog.setText("¿Está seguro que desea eliminar este registro de horas?");
                dialog.setConfirmText("Sí, eliminar");
                dialog.setCancelText("Cancelar");

                dialog.addConfirmListener(ev -> {
                    horasService.delete(registro);
                    actualizarGrid();
                    limpiarFormulario();
                    Notification.show("🗑 Registro eliminado", 3000, Notification.Position.MIDDLE);
                });

                dialog.open();
            });

            eliminar.getStyle()
                .set("background-color", "#dc2626")
                .set("color", "white")
                .set("font-size", "13px")
                .set("border-radius", "6px");

            return new HorizontalLayout(editar, eliminar);
        }).setHeader("Acciones");

        grid.setAllRowsVisible(true);
        grid.setWidthFull();
        grid.getStyle().set("overflow-x", "auto");
    }

    private FormLayout formulario() {
        return new FormLayout(
            estudianteCombo, ingreso, salida,
            horasTrabajadas, actividades
        );
    }

    private HorizontalLayout botones() {
        guardar.addClickListener(e -> {
            if (estudianteCombo.isEmpty() || ingreso.isEmpty() || salida.isEmpty()) {
                Notification.show("Por favor complete todos los campos obligatorios", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (registroActual == null) {
                registroActual = new ControlHoras();
            }

            registroActual.setEstudiante(estudianteCombo.getValue());
            registroActual.setFechaHoraIngreso(ingreso.getValue());
            registroActual.setFechaHoraSalida(salida.getValue());
            registroActual.setHorasTrabajadas(
                horasTrabajadas.getValue() != null ? BigDecimal.valueOf(horasTrabajadas.getValue()) : BigDecimal.ZERO
            );
            registroActual.setActividadesRealizadas(actividades.getValue());

            horasService.save(registroActual);
            Notification.show("✅ Registro guardado con éxito", 3000, Notification.Position.MIDDLE);
            actualizarGrid();
            limpiarFormulario();
        });

        limpiar.addClickListener(e -> limpiarFormulario());

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

        HorizontalLayout botones = new HorizontalLayout(guardar, limpiar);
        botones.setJustifyContentMode(JustifyContentMode.CENTER);
        botones.setWidthFull();
        return botones;
    }

    private void cargarFormulario(ControlHoras registro) {
        this.registroActual = registro;
        estudianteCombo.setValue(registro.getEstudiante());
        ingreso.setValue(registro.getFechaHoraIngreso());
        salida.setValue(registro.getFechaHoraSalida());
        horasTrabajadas.setValue(registro.getHorasTrabajadas().doubleValue());
        actividades.setValue(registro.getActividadesRealizadas());
    }

    private void limpiarFormulario() {
        registroActual = null;
        estudianteCombo.clear();
        ingreso.clear();
        salida.clear();
        horasTrabajadas.clear();
        actividades.clear();
    }

    private void actualizarGrid() {
        grid.setItems(horasService.findAll());
    }

    private void configurarFormulario() {
        estudianteCombo.setItems(estudianteService.findAll());
        estudianteCombo.setItemLabelGenerator(e -> e.getCarnet());
    }

    private void configurarBotones() {
        guardar.setWidth("150px");
        limpiar.setWidth("150px");
    }
}
