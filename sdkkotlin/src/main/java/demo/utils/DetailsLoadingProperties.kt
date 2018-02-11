package demo.utils

import ag.sportradar.sdk.core.model.DetailsParams
import ag.sportradar.sdk.sports.model.*
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportSeasonDetails
import ag.sportradar.sdk.sports.model.motostport.AnyMotorsportStageDetails
import ag.sportradar.sdk.sports.model.motostport.formulaone.FormulaOneRaceStageDetailsParams
import ag.sportradar.sdk.sports.model.motostport.formulaone.FormulaOneSeasonDetailsParams

/**
 * Created by rokoblak on 1/10/18.
 * The different sets of details loading parameters, one for each panel
 */
object DetailsLoadingProperties {

    /**
     * Match details
     */
    val Statistics = GenericMatchDetailsParams(LoadingProperties.Statistics, LoadingProperties.PeriodStatistics)
    val TeamTables = GenericMatchDetailsParams(LoadingProperties.Team1LeagueTable, LoadingProperties.Team2LeagueTable)
    val Events = GenericMatchDetailsParams(LoadingProperties.Events)
    val Lineups = GenericMatchDetailsParams(LoadingProperties.Lineups)
    val Odds = GenericMatchDetailsParams(LoadingProperties.Odds)
    val MatchInfo = GenericMatchDetailsParams(LoadingProperties.Staff, LoadingProperties.Venue, LoadingProperties.Attendance)

    /**
     * Team details
     */
    val TeamLastNextMatches = GenericTeamDetailsParams(LoadingProperties.LastMatches, LoadingProperties.NextMatches)
    val TeamInfo = GenericTeamDetailsParams(LoadingProperties.HomeVenue, LoadingProperties.Tournaments, LoadingProperties.Competitions, LoadingProperties.HomeVenue, LoadingProperties.Manager)
    val TeamSquad = GenericTeamDetailsParams(LoadingProperties.Squad)

    /**
     * Tournament details
     */
    val TournamentSeason = GenericTeamTournamentDetailsParams(LoadingProperties.Seasons, LoadingProperties.ActiveSeason)
    val TournamentTeams = GenericTeamTournamentDetailsParams(LoadingProperties.Teams, LoadingProperties.DefendingChampion)

    /**
     * Stage details
     */
    val StageLiveRankingTable = GenericTeamStageDetailsParams(LoadingProperties.LiveRankingTable)
    val StageCupRosters = GenericTeamStageDetailsParams(LoadingProperties.CupRosters)

    /**
     * Season details
     */
    val SeasonFixtures = GenericTeamSeasonDetailsParams(LoadingProperties.Fixture)
    val SeasonRankingTable = GenericTeamSeasonDetailsParams(LoadingProperties.RankingTables)
    val SeasonLiveTable = GenericTeamSeasonDetailsParams(LoadingProperties.LiveRankingTables)
    val SeasonPlayerStatistics = GenericTeamSeasonDetailsParams(LoadingProperties.PlayerStats)
    val SeasonGoalsStatistics = GenericTeamSeasonDetailsParams(LoadingProperties.Goals, LoadingProperties.OwnGoals)
    val SeasonCardsStatistics = GenericTeamSeasonDetailsParams(LoadingProperties.Cards)
    val SeasonAssistsStatistics = GenericTeamSeasonDetailsParams(LoadingProperties.Assists)

    /**
     * Player details
     */
    val PlayerSeasons = GenericTeamPlayerDetailsParams(LoadingProperties.Seasons)
    val PlayerStats = GenericTeamPlayerDetailsParams(LoadingProperties.TotalStats, LoadingProperties.TeamStatistics)
    val Tournaments = GenericTeamPlayerDetailsParams(LoadingProperties.Tournaments)
    val Teams = GenericTeamPlayerDetailsParams(LoadingProperties.Teams)
    val Roles = GenericTeamPlayerDetailsParams(LoadingProperties.Roles)

    /**
     * Motorsport season/stage details
     */
    val F1Competitors: DetailsParams<AnyMotorsportSeasonDetails> = FormulaOneSeasonDetailsParams().includeDrivers() as DetailsParams<AnyMotorsportSeasonDetails>
    val F1SeasonTeams: DetailsParams<AnyMotorsportSeasonDetails> = FormulaOneSeasonDetailsParams().includeTeams() as DetailsParams<AnyMotorsportSeasonDetails>
    val F1SeasonTeamStatistics: DetailsParams<AnyMotorsportSeasonDetails> = FormulaOneSeasonDetailsParams().includeTeamStatistics() as DetailsParams<AnyMotorsportSeasonDetails>
    val F1SeasonDriverStatistics: DetailsParams<AnyMotorsportSeasonDetails> = FormulaOneSeasonDetailsParams().includeDriverStatistics() as DetailsParams<AnyMotorsportSeasonDetails>
    val F1Stages: DetailsParams<AnyMotorsportSeasonDetails> = FormulaOneSeasonDetailsParams().includeStages() as DetailsParams<AnyMotorsportSeasonDetails>

    val F1StageDriversStats: DetailsParams<AnyMotorsportStageDetails> = FormulaOneRaceStageDetailsParams().includeDriverStatistics() as DetailsParams<AnyMotorsportStageDetails>
    val F1StageTeamStats: DetailsParams<AnyMotorsportStageDetails> = FormulaOneRaceStageDetailsParams().includeTeamStatistics() as DetailsParams<AnyMotorsportStageDetails>
    val F1StageDrivers: DetailsParams<AnyMotorsportStageDetails> = FormulaOneRaceStageDetailsParams().includeDrivers() as DetailsParams<AnyMotorsportStageDetails>
    val F1StageRaceEvents: DetailsParams<AnyMotorsportStageDetails> = FormulaOneRaceStageDetailsParams().includePractices().includeQualifications().includeRace() as DetailsParams<AnyMotorsportStageDetails>
    val F1StageTeams: DetailsParams<AnyMotorsportStageDetails> = FormulaOneRaceStageDetailsParams().includeTeams() as DetailsParams<AnyMotorsportStageDetails>
}