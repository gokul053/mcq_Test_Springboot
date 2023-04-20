package com.concertidc.mcqtest.dto;

import java.util.List;

public class MarksFilter {

	private List<UsersDto> passedCandidates;
	private List<UsersDto> failedCandidates;

	public List<UsersDto> getPassedCandidates() {
		return passedCandidates;
	}

	public void setPassedCandidates(List<UsersDto> passedCandidates) {
		this.passedCandidates = passedCandidates;
	}

	public List<UsersDto> getFailedCandidates() {
		return failedCandidates;
	}

	public void setFailedCandidates(List<UsersDto> failedCandidates) {
		this.failedCandidates = failedCandidates;
	}

	public MarksFilter() {
	}

	public MarksFilter(List<UsersDto> passedCandidates, List<UsersDto> failedCandidates) {
		this.passedCandidates = passedCandidates;
		this.failedCandidates = failedCandidates;
	}

}
