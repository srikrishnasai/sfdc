package com.tadigital.sfdc_campaign.repo;

import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpServerConnection {

	public static final String SFTPWORKINGDIR = "acs";

	public static void main(String[] args) {
		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession("sfdc", "sfdc.tadigital.com", 22);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword("sfdc$1234");
			System.out.println("Establishing Connection...");
			session.connect();
			System.out.println("Connection established.");
			Channel channel = session.openChannel("sftp");
			channel.connect();
			System.out.println("SFTP Channel created.");
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			Vector filelist = sftpChannel.ls(SFTPWORKINGDIR);
			for (int i = 0; i < filelist.size(); i++) {
				LsEntry entry = (LsEntry) filelist.get(i);
				System.out.println(entry.getFilename());
			}
			sftpChannel.exit();

		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
	}

}