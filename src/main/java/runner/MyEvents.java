package runner;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyEvents
{

	// generate stuff
	LocalDateTime time = LocalDateTime.now();
	AnimeList recommendationList;
	IDiscordClient parentClient;

	public MyEvents(IDiscordClient cli)
	{
		parentClient = cli;
		recommendationList = new AnimeList();

	}

	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent event)
	{

		// message split
		String[] argArray = event.getMessage().getContent().split(" ");

		if (argArray.length == 0)
		{
			return;
		}

		if (!argArray[0].startsWith(BotUtils.BOT_PREFIX))
		{
			return;
		}

		String commandStr = argArray[0].substring(1);

		// konvertieren zur Arraylist
		List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
		argsList.remove(0); // Remove the command

		switch (commandStr)
		{

			case "test":
				testCommand(event, argsList);
			break;
			case "help":
				sendBuild(event, argsList);
			break;
			case "giveAnime":
				giveAnime(event, argsList);
			break;
			case "addAnime":
				addAnime(event, argsList);
			break;

		}

	}

	@EventSubscriber
	public void isReady(ReadyEvent event)
	{
		BotUtils.sendMessage(event.getClient().getChannels().get(3), "Yami is ready");
	}


	@EventSubscriber
	public void welcome(UserJoinEvent event)
	{
		LocalDateTime time = event.getJoinTime();
		BotUtils.sendMessage(event.getClient().getChannels().get(0),
		        event.getUser().getName() + " joined at: " + time.toString() + ". Welcome!");
	}

	@EventSubscriber
	public void joinMessage(UserVoiceChannelJoinEvent event)
	{
		IVoiceChannel chan = event.getVoiceChannel();
		IChannel gen = event.getClient().getChannels().get(0);
		IUser usr = event.getUser();

		BotUtils.sendMessage(gen, usr.getName() + " joined " + chan.getName());
	}

	@EventSubscriber
	public void leaveMessage(UserVoiceChannelLeaveEvent event)
	{
		IVoiceChannel chan = event.getVoiceChannel();
		IChannel gen = event.getClient().getChannels().get(0);
		IUser usr = event.getUser();

		BotUtils.sendMessage(gen, usr.getName() + " left " + chan.getName());
	}

	@EventSubscriber
	public void moveMessage(UserVoiceChannelMoveEvent event)
	{
		IVoiceChannel oldChan = event.getOldChannel();
		IVoiceChannel newChan = event.getNewChannel();
		IChannel gen = event.getClient().getChannels().get(0);
		IUser usr = event.getUser();

		BotUtils.sendMessage(gen, usr.getName() + " moved from " + oldChan.getName() + " to " + newChan.getName());

	}


	private void testCommand(MessageReceivedEvent event, List<String> args)
	{

		BotUtils.sendMessage(event.getChannel(), "You ran the test command with args: " + args);

	}

	private void sendBuild(MessageReceivedEvent event, List<String> args)
	{

		EmbedBuilder builder = new EmbedBuilder();

		builder.withTitle("Commands");
		builder.appendField("!help:", "shows this message", false);
		builder.appendField("!giveAnime", "gives random animeshow from archive", false);
		builder.appendField("!addAnime", "adds name link into archive. Syntax: name proxerlink", false);
		builder.appendField("Author: Hoppix#6723", "[@Github](https://github.com/Hoppix)", false);
		builder.appendField("Made with Discord4J", "[@Github](https://github.com/austinv11/Discord4J)", false);
		builder.withColor(255, 0, 0);
		builder.withTitle("Yami-Bot");
		builder.withTimestamp(time);
		BotUtils.sendBuild(event.getChannel(), builder);
	}

	private void giveAnime(MessageReceivedEvent event, List<String> args)
	{
		if(recommendationList.getAnimeList().isEmpty())
		{
			BotUtils.sendMessage(event.getChannel(), "No anime in current archive.");
			BotUtils.sendMessage(event.getChannel(), "use !addAnime name proxerlink to add into archive");
			return;
		}
		
		String anime = recommendationList.getRandomProxerAnime().toString();
		BotUtils.sendMessage(event.getChannel(), anime);				
	}
	
	private void addAnime(MessageReceivedEvent event, List<String> args)
	{
		if(args.size() == 0)
		{
			BotUtils.sendMessage(event.getChannel(), "No name given!");
			return;
		}
		
		if(args.size() == 1)
		{
			BotUtils.sendMessage(event.getChannel(), "No link given!");
			return;
		}
		//why dis?
		String name; 
		name = args.get(0);
		String link;
		link = args.get(1);		
		
		
		try
		{
			recommendationList.addProxerAnime(name, link);
			BotUtils.sendMessage(event.getChannel(), "Anime " + name + " added to archive.");
		}
		catch (MalformedURLException e)
		{
			BotUtils.sendMessage(event.getChannel(), "Must be complete URL!");
			e.printStackTrace();
		}
		catch (WrongLinkException e)
		{
			BotUtils.sendMessage(event.getChannel(), "Must be ProxerLink!");
			e.printStackTrace();
		}
		
	}

}
