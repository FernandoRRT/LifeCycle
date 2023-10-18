package br.edu.scl.ifsp.sdm.lifecycle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.EditText
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import br.edu.scl.ifsp.sdm.lifecycle.databinding.ActivityMainBinding
import br.edu.scl.ifsp.sdm.lifecycle.databinding.TilePhoneBinding

class MainActivity : AppCompatActivity() {
    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var filledChars: Int = 0

    //o "object :" cria uma classe anônima que herda de Handler e sobrescreve o método handleMessage
    //o Looper é responsável por executar mensagens e runnable objects na thread.
    // !!: O operador de "not null assertion" em Kotlin. Isso significa que você está afirmando que o resultado de myLooper() não é nulo. Se for nulo, o programa lançará uma exceção.
    private val nameHandler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            //let { ... }: O let é uma função de escopo em Kotlin. O código dentro do let será executado se getString(NAME) retornar um valor não nulo.
            msg.data.getString(NAME).let {
                activityMainBinding.nameEt.setText(it)
            }
        }
    }


    companion object {
        const val PHONES = "PHONES"
        const val FILLED_CHARS = "FILLED_CHARS"
        const val NAME = "NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        activityMainBinding.apply {
            setSupportActionBar(toolbarIn.toolbar)

            nameEt.doAfterTextChanged {
                "${getString(R.string.filled_chars)} ${++filledChars}".also {
                    filledCharsTv.text = it
                }
            }

            addPhoneBt.setOnClickListener {
                val tilePhoneBinding = TilePhoneBinding.inflate(layoutInflater)
                //vai adicionar uma nova view no layout
                phonesLl.addView(tilePhoneBinding.root)
            }
            openAnotherActivityBt.setOnClickListener {
                startActivity(Intent(this@MainActivity, AnotherActivity::class.java))
            }
        }
        supportActionBar?.setSubtitle(R.string.main)

        //Criando uma thread para simular um processo demorado. Como um acesso a banco de dados ou uma requisição de rede.
        Thread {
            Thread.sleep(3000)
            //isso vai gerar um erro aqui, pois esse não é o lugar de colocar esse tipo de requisição.
            //Apenas a thread que criou a view pode manipular a view.
            //activityMainBinding.nameEt.setText("Fernando")
            //Para resolver isso, eu posso usar o runOnUiThread

            //runOnUiThread {
            //    activityMainBinding.nameEt.setText("Fernando")
            //}


            // Usando a função de escopo also para executar ações com o objeto nameHandler, sem modificar seu valor original.
            // O bloco dentro de also será executado e, no final, nameHandler será retornado inalterado.
            nameHandler.also {
                // Envia uma mensagem para a fila de mensagens (message queue) associada ao nameHandler.
                // Essa mensagem será processada posteriormente pelo método handleMessage() do nameHandler.
                it.sendMessage(Message.obtain(it).apply {

                    // Cria um Bundle, que é uma coleção de pares chave-valor, para armazenar dados a serem enviados na mensagem.
                    // O Bundle é comumente usado em Android para passar dados entre diferentes componentes, como Activities e Handlers.
                    data = Bundle().apply {

                        // Coloca uma string com a chave NAME e o valor "SDM" dentro do Bundle.
                        // Isso permite que a string seja recuperada quando a mensagem for processada.
                        putString(NAME, "SDM")
                    }
                })
            }

            Log.v(getString(R.string.app_name), "Main - onCreate(): Thread finalizada")
        }.start()

        Log.v(getString(R.string.app_name), "Main - onCreate(): Início COMPLETO")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(FILLED_CHARS, filledChars)

        val phones = mutableListOf<String>()
        activityMainBinding.phonesLl.children.forEachIndexed { index, view ->
            if (index == 0) {
                (view as EditText).text.toString().let { phones.add(it) }
            }
        }
        outState.putStringArray(PHONES, phones.toTypedArray())

        Log.v(getString(R.string.app_name), "Main - onSaveInstanceState(): Salvando estado")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        //no caso aqui eu coloquei um valor padrão para não ter que tratar nulo. Mas não precisa colocar o defaultValue 0
        //filledChars = savedInstanceState.getInt(FILLED_CHARS, 0) posso usar dessa forma.

        //ou dessa. Pois vou usar o valor de filledChars para preencher o textview
        savedInstanceState.getInt(FILLED_CHARS, 0).let {
            filledChars = it
            "${getString(R.string.filled_chars)} $filledChars".also { fc ->
                //renomeei a variável de it para fc. Só pra difenciar mesmo.
                activityMainBinding.filledCharsTv.text = fc
            }
        }

        // Obtenho o array de strings armazenado em savedInstanceState e, para cada item (número de telefone), adiciono uma nova view ao layout.
        savedInstanceState.getStringArray(PHONES)?.forEach { phone ->
            // Instancio a classe TilePhoneBinding, passando o layoutInflater para inflar o layout do tile_phone.
            TilePhoneBinding.inflate(layoutInflater).apply {
                // Defino o texto do EditText com o valor do telefone atual.
                // Uso 'root' para referenciar a raiz do layout XML, que neste caso é o EditText.
                // Não tenho acesso direto ao EditText, pois não criei uma ID ou variável específica para ele.
                root.setText(phone)

                // Adiciono a view inflada (root) ao layout 'phonesLl' da Activity, que contém todas as views de telefone.
                activityMainBinding.phonesLl.addView(root)
            }
        }
        Log.v(getString(R.string.app_name), "Main - onRestoreInstanceState(): Restaurando estado")
    }

    override fun onStart() {
        super.onStart()
        Log.v(getString(R.string.app_name), "Main - onStart(): Início VISÍVEL")
    }

    override fun onResume() {
        super.onResume()
        Log.v(getString(R.string.app_name), "Main - onResume(): PRIMEIRO PLANO")
    }

    override fun onPause() {
        super.onPause()
        Log.v(getString(R.string.app_name), "Main - onPause(): FIM DO CICLO PRIMEIRO PLANO")
    }

    override fun onStop() {
        super.onStop()
        Log.v(getString(R.string.app_name), "Main - onStop(): fim do CICLO DE VIDA VISÍVEL")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v(getString(R.string.app_name), "Main - onDestroy(): fim COMPLETO")
    }

    override fun onRestart() {
        super.onRestart()
        Log.v(getString(R.string.app_name), "Main - onRestart(): preparando OnStart()")
    }

}

