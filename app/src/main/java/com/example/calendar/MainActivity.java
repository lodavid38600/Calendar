package com.example.calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Classe principale représentant l'activité du calendrier
public class MainActivity extends AppCompatActivity {

    // Déclarations des composants d'interface utilisateur
    private RecyclerView eventsRecyclerView;
    private Button addEventButton;
    private EventAdapter eventAdapter;
    private List<Event> eventsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Associer le layout à cette activité
        setContentView(R.layout.activity_main);

        // Initialisation du RecyclerView pour afficher les événements
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRecyclerView.setHasFixedSize(true);

        // Initialisation de l'adaptateur pour le RecyclerView
        eventAdapter = new EventAdapter(eventsList);
        eventsRecyclerView.setAdapter(eventAdapter);

        // Initialisation du bouton d'ajout d'événement
        addEventButton = findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher le dialogue pour créer un nouvel événement
                showCreateEventDialog();
            }
        });

        // Gestionnaire d'événements pour un clic long sur un élément de la liste
        eventAdapter.setOnItemLongClickListener(event -> {
            // Supprimer l'événement de la liste
            eventsList.remove(event);

            // Mettre à jour l'adaptateur avec la nouvelle liste
            eventAdapter.setEvents(eventsList);

            // Afficher une notification pour informer l'utilisateur
            Toast.makeText(MainActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();

            return true; // Indique que l'événement de long-clic a été consommé
        });
    }

    // Fonction pour afficher le dialogue de création d'événement
    private void showCreateEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_create_event, null);

        // Initialisation des composants du dialogue
        EditText editTextTitle = view.findViewById(R.id.edit_text_title);
        EditText editTextDescription = view.findViewById(R.id.edit_text_description);
        DatePicker datePicker = view.findViewById(R.id.date_picker);
        TimePicker timePicker = view.findViewById(R.id.time_picker);

        builder.setView(view);
        builder.setTitle("Créer un événement");

        builder.setPositiveButton("Add", (dialog, which) -> {
            // Récupération des informations saisies par l'utilisateur
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            // Création d'un objet Calendar pour stocker la date et l'heure de l'événement
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute);

            if(title.isEmpty()) {
                // Vérifier si le titre est vide
                Toast.makeText(MainActivity.this, "Le titre ne peux pas être vide", Toast.LENGTH_SHORT).show();
                return;
            }

            // Création d'un nouvel événement avec les informations saisies
            Event newEvent = new Event(title, description, new SimpleDateFormat("dd-MM-yyyy HH:mm").format(calendar.getTime()));
            eventsList.add(newEvent);
            eventAdapter.setEvents(eventsList);

            // Notifier l'adaptateur que les données ont changé
            eventAdapter.notifyDataSetChanged();

            // Fermer le dialogue
            dialog.dismiss();
        });

        // Ajouter un bouton d'annulation pour fermer le dialogue sans ajouter d'événement
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        // Création et affichage du dialogue
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
