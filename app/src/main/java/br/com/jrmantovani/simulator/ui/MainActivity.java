package br.com.jrmantovani.simulator.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Random;

import br.com.jrmantovani.simulator.R;
import br.com.jrmantovani.simulator.adapter.MatchesAdapter;
import br.com.jrmantovani.simulator.data.MatchesAPI;
import br.com.jrmantovani.simulator.databinding.ActivityMainBinding;
import br.com.jrmantovani.simulator.domain.Match;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;





public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MatchesAPI matchesApi;
    private MatchesAdapter matchesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupHttpClient();
        setupMatchesList();
        setupMatchesRefresh();
        setupFlaatingActiconButton();
    }

    private void setupHttpClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jrodolfom.github.io/matches-smulator-api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

         matchesApi = retrofit.create(MatchesAPI.class);
    }

    private void setupMatchesList(){

        binding.rwMactches.setHasFixedSize(true);
        binding.rwMactches.setLayoutManager(new LinearLayoutManager(this));

        findMatchesFromApi();
    }



    private void setupMatchesRefresh(){
       binding.srlMatches.setOnRefreshListener(this::findMatchesFromApi);
    }
    private void  setupFlaatingActiconButton(){
       binding.fabSimulate.setOnClickListener(view -> {
           view.animate().rotationBy(360).setDuration(500).setListener(new AnimatorListenerAdapter() {
               @Override
               public void onAnimationEnd(Animator animation) {
                   Random random = new Random();
                  for(int i=0; i < matchesAdapter.getItemCount(); i++){
                      Match match = matchesAdapter.getMatches().get(i);
                      match.getAwayTeam().setScore(random.nextInt(match.getAwayTeam().getStars() + 1));
                      match.getHomeTeam().setScore(random.nextInt(match.getHomeTeam().getStars() + 1));
                      matchesAdapter.notifyItemChanged(i);
                  }
               }
           });
       });
    }
    private void showErrorMessage(){
        Snackbar.make(binding.fabSimulate, R.string.error_api,Snackbar.LENGTH_LONG).show();
    }
    private void findMatchesFromApi() {
        binding.srlMatches.setRefreshing(true);
        matchesApi.getMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if(response.isSuccessful()){
                    List<Match> matches = response.body();
                    matchesAdapter = new MatchesAdapter(matches);
                    binding.rwMactches.setAdapter(matchesAdapter);
                }else{
                    showErrorMessage();
                    Log.i("SIMULADOR", "Erro");
                }
                binding.srlMatches.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                showErrorMessage();
                binding.srlMatches.setRefreshing(false);
                Log.i("SIMULADOR", "Erro");

            }
        });
    }
}
