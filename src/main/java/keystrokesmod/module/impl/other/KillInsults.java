package keystrokesmod.module.impl.other;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KillInsults extends Module {
    private SliderSetting mode;
    private ButtonSetting global;
    private ButtonSetting tell;
    private String[] modes = new String[]{"Mush"};
    private final String[] aaaa = new String[]{"%s foi visitar o technoblade",
            "%s estou surpreso que você conseguiu apertar o botão de jogar",
            "%s como você consegue jogar com o seu qi de menos de 30?",
            "%s você é o tipo de pessoa que fica em terceiro lugar em um 1v1",
            "%s eu recomendaria que você voltasse pro roblox",
            "%s minecraft não é pra todo mundo, volta para o fortnite",
            "%s to mijando na sua arvore genealógica",
            "obrigado pela rosa! %s",
            "obrigado pela rosa, %s",
            "%s pare por favor",
            "%s," + "eu diria desinstalar, mas você provavelmente erraria isso também",
            "pare de respirar, seu idiota %s",
            "%s calma com a pintura, mano",
            "alguém pode dar um lenço a esse menino, %s está quase a chorar",
            "isso foi uma #VictoryRoyale!, boa sorte na próxima vez, %s",
            "minha avó joga minecraft muito melhor do que você %s", "%s eu sinto muito.",
            "se o corpo é 70%% de água, como %s é 100%% sal????",
            "%s você sabe que os jogadores cegos também merecem uma chance, eu te apoio",
            "isso foi realmente uma jogada muito ruim %s",
            "%s você consegue ao menos acertar um player parado?",
            "ei %s, quem deixou a sua jaula aberta?!",
            "alguém em 1940 esqueceu de botar gás em você, %s",
            "%s: eu sou preto e isso é um assalto",
            "%s, eu entendo porque os seus parentes abusaram de você",
            "um momento de silêncio para o %s",
            "%s, você é a inspiração pro aborto",
            "%s, você realmente gosta assim tanto de morrer?",
            "se eu tivesse escolha entre %s e felipe neto, eu escolheria o felipe neto",
            "ei %s, o que o seu QI e as suas kills têm em comum? ambos são baixos pra caralho",
            "ei %s, quer umas dicas de PvP?", "%s por favor, seja tóxico comigo, eu gosto disso",
            "uau %s, você acabou de morrer em um jogo de legos",
            "estou surpreso que você tenha conseguido apertar o botão 'Instalar' %s",
            "%s é uma skin default!!!1!1!1!",
            "%s você morreu na porra de um jogo quadrado",
            "%s gosta de anime",
            "%s ruim, você quase nem me bateu",
            "%s, sua árvore genealógica deve ser um cacto",
            "%s alguns garotos foram abandonados ao nascer, mas você foi claramente jogado em uma parede",
            "obrigado pela kill de graça %s",
            "%s você está sequer tentando?",
            "%s Você. é. Pessimo.",
            "minha mãe é melhor nisso do que você %s",
            "%s me adicione para que possamos falar sobre como você é inútil",
            "%s: 'Staff! Staff! Ajudem-me! Eu sou uma porcaria neste jogo e estou ficando bravo!'",
            "%s é realmente tão difícil mirar em mim enquanto estou bhoppando ao seu redor?",
            "%s, Vape é uma coisa legal que você deveria pesquisar sobre",
            "%s eu não estou usando reach, você só precisa clicar mais rápido",
            "%s eu espero que você tenha gravado isso, para que você possa ver como você realmente é lixo.",
            "%s você tem que usar o botão esquerdo e direito do mouse neste jogo, caso tenha esquecido",
            "%s a quantidade de ping que você tem equivale ás suas células cerebrais",
            "%s ALT+F4 para remover o problema",
            "%s ALT+F4 para um easter egg muito oculto!!1111!1",
            "%s volte para o Fortnite, onde você pertence, seu degenerado de 5 anos de idade",
            "%s bottom text melhor do que você!1",
            "%s eu sou um verdadeiro gamer, e você acabou de ser morto!!",
            "%s como você é ruim. estou perdendo células cerebrais só de te ver jogar",
            "%s pule do prédio de sua escola com uma corda ao redor do pescoço",
            "%s não, você não é cego! Eu TE MATEI!",
            "%s fácil 10 de coração L",
            "%s é quase como se eu pudesse ouvir você gritar do outro lado",
            "a contagem de cromossomos em %s duplica o tamanho deste jogo",
            "um milhão de anos de evolução e temos pessoas como o %s",
            "%s deu rage",
            "%s, eu jogo fortnite dupla com sua mãe",
            "%s bate com força, mas o pai dele o bate com mais força",
            "%s desista de viver",
            "como você apertou o botão DOWNLOAD com essa mira? %s",
            "eu diria que sua mira é câncer, mas pelo menos o câncer mata pessoas %s",
            "%s é quase tão útil quanto pedais em uma cadeira de rodas",
            "a Mira do %s agora é patrocinado pelo Parkinson!",
            "%s Por favor, você não poderia se comprometer a não morrer, senhor, obrigado",
            "%s você provavelmente chupa as maçanetas das portas",
            "%s pare de respirar seu burro",
            "%s :batata:",
            "%s Super Mario Bros. som da morte",
            "%s knock knock, FBI abra a porta, vimos você procura por vape cracked",
            "%s por favor pule da janela por vip de graça",
            "%s você nem sequer teve chance!",
            "e o %s continua tentando!",
            "%s, não estou dizendo que sua vida não vale nada, mas eu desligaria seu suporte de vida",
            "eu não sabia que morrer era uma habilidade especial %s",
            "%s, Stephen Hawking tinha melhor coordenação motora do que você",
            "%s lol GG!!!",
            "%s gg e.z criança",
            "%s gg guys obrigado pela minha primeira kill!",
            "não se esqueça de me reportar %s", "seu QI é o de um Steve %s",
            "não estou dizendo que você não vale nada, mas eu desligaria seu suporte de vida para carregar",
            "%s 2 mais 2 é 4, menos 1 esse é o seu QI",
            "acho que você precisa de vape %s!",
            "%s, meu avô cego com parkinson tem uma mira melhor do que você",
            "o fortnite está perdendo uma estrela. volte para o seu lugar, %s",
            "%s, estou perdendo pontos de QI só de vê-lo jogar",
            "os preservativos deveriam ter te pago pela campanha de marketing, %s",
            "%s, seus pais o abandonaram, e o orfanato fez o mesmo",
            "os preservativos realmente perderam uma oportunidade de propaganda com você",
            "uma salva de palmas para %s, que não para de tentar",
            "%s por favor considere não viver",
            "%s é o tipo de pessoa de assassinar alguém e pedir desculpas dizendo que foi um acidente",
            "%s, obteve um F no teste de qi",
			"%s, eu sou o maior passaro!",
            "%s, crianças como você foram a inspiração para o aborto",
            "%s seus dentes são como estrelas - dourados, e separados",
            "rosas são azuis, violetas são vermelhas, %s acabou de morrer",
            "%s eu não uso hack porque o CRIS está vigiando então ele me baniria",
            "%s você morreu para o melhor hack do jogo, agora com bypass infinito de sprint!"};
    private ArrayList<String> aaa = new ArrayList<>();
    private EntityLivingBase entity;
    private ArrayDeque<String> messageQueue = new ArrayDeque<>();
    private final AtomicLong lastMessageTime = new AtomicLong(0L);
    
    public KillInsults() {
        super("KillInsults", category.other, 0);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(tell = new ButtonSetting("/tell", false));
        this.registerSetting(global = new ButtonSetting("/g", false));
        aaa.addAll(Arrays.asList(aaaa));
    }

    @SubscribeEvent
    public void onReceivePacket(ReceivePacketEvent event) {
        sendMessageWithDelay();

        if (event.getPacket() instanceof S02PacketChat) {
            String playerName;
            S02PacketChat s02 = (S02PacketChat) event.getPacket();
            String text = s02.getChatComponent().getUnformattedText();

            if (text.matches("^MORTE Você matou (.+) \\(Não conta\\)\\.")) {
                playerName = this.extract(text, "^MORTE Voc\u00ea matou (.+) \\(N\u00e3o conta\\)\\.");
                try {
                    this.addToQueue(String.format(this.aaa.remove(0), playerName));
                } catch (UnknownFormatConversionException e) {
                }
            }
            if (text.matches("^MORTE Você matou ([\\w]+)\\.$")) {
                playerName = this.extract(text, "^MORTE Você matou ([\\w]+)\\.$");
                try {
                    this.addToQueue(String.format(this.aaa.remove(0), playerName));
                } catch (UnknownFormatConversionException e) {
                }
            }
            if (text.matches("^(.+) foi jogado no void por " + mc.thePlayer.getGameProfile().getName() + "\\.(?: KILL FINAL!)?$") && !(playerName = this.extract(text, "^(.+) foi jogado no void por " + mc.thePlayer.getGameProfile().getName() + "\\.(?: KILL FINAL!)?$")).contains(" ")) {
                try {
                    this.addToQueue(String.format(this.aaa.remove(0), playerName));
                } catch (UnknownFormatConversionException e) {
                }
            }
            if (text.matches("^(.+) caiu no void por " + mc.thePlayer.getGameProfile().getName() + "\\.(?: KILL FINAL!)?$") && !(playerName = this.extract(text, "^(.+) caiu no void por " + mc.thePlayer.getGameProfile().getName() + "\\.(?: KILL FINAL!)?$")).contains(" ")) {
                try {
                    this.addToQueue(String.format(this.aaa.remove(0), playerName));
                } catch (UnknownFormatConversionException e) {
                }
            }
            if (text.matches("^(.+) morreu para " + mc.thePlayer.getGameProfile().getName() + "\\.(?: KILL FINAL!)?$") && !(playerName = this.extract(text, "^(.+) morreu para " + mc.thePlayer.getGameProfile().getName() + "\\.(?: KILL FINAL!)?$")).contains(" ")) {
                try {
                    this.addToQueue(String.format(this.aaa.remove(0), playerName));
                } catch (UnknownFormatConversionException e) {
                }
            }
        }
    }

    private String extract(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    void addToQueue(String message) {
        messageQueue.offer(message);
    }

    private void sendMessageWithDelay() {
        long delay = 3200;
        long lastMessageTimeValue = lastMessageTime.get();
        long currentTime = System.currentTimeMillis();
        if (!messageQueue.isEmpty() && currentTime - lastMessageTimeValue >= delay) {
            String message = this.messageQueue.poll();
            if (message != null) {
                if (global.isToggled()) {
                    message = "/g " + message;
                }
                if (tell.isToggled()) {
                    message = "/tell " + message;
                }
                mc.thePlayer.sendChatMessage(message);
                lastMessageTime.set(currentTime);
                if (aaa.isEmpty()) {
                    aaa.addAll(Arrays.asList(aaaa));
                    Collections.shuffle(aaa);
                }
            }
        }
    }
}
