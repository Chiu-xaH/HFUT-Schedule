param(
    [ValidateSet("Today", "Date", "Days", "All", "Uncommitted")]
    [string]$Mode = "Today",

    [string]$Date,

    [ValidateRange(1, 36500)]
    [int]$Days = 30,

    [string]$Author,

    [switch]$AllAuthors,

    [string]$Repo = "."
)

$ErrorActionPreference = "Stop"

function Assert-GitRepository {
    & git -C $Repo rev-parse --is-inside-work-tree 2>$null | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "Not a Git repository: $Repo"
    }
}

function Get-AuthorArguments {
    if ($AllAuthors -or $Mode -eq "Uncommitted") {
        return @()
    }

    $value = $Author
    if ([string]::IsNullOrWhiteSpace($value)) {
        $value = (& git -C $Repo config user.email 2>$null | Select-Object -First 1)
    }
    if ([string]::IsNullOrWhiteSpace($value)) {
        $value = (& git -C $Repo config user.name 2>$null | Select-Object -First 1)
    }
    if ([string]::IsNullOrWhiteSpace($value)) {
        throw "Git user is not configured. Use -Author or -AllAuthors."
    }

    return @("--author=$value")
}

function Get-LineTotal {
    param([string[]]$Lines)

    [long]$added = 0
    [long]$deleted = 0

    foreach ($line in $Lines) {
        if ($line -match '^(\d+)\s+(\d+)\s+') {
            $added += [long]$Matches[1]
            $deleted += [long]$Matches[2]
        }
    }

    return [PSCustomObject]@{
        Added   = $added
        Deleted = $deleted
        Changed = $added + $deleted
        Net     = $added - $deleted
    }
}

function Write-LineTotal {
    param($Total)

    Write-Host "Added:   $($Total.Added) lines"
    Write-Host "Deleted: $($Total.Deleted) lines"
    Write-Host "Changed: $($Total.Changed) lines"
    Write-Host ("Net:     {0:+0;-0;0} lines" -f $Total.Net)
}

function Write-DailyStats {
    param([string[]]$Lines)

    $stats = @{}
    $currentDate = $null

    foreach ($line in $Lines) {
        if ($line -match '^@@@(\d{4}-\d{2}-\d{2})$') {
            $currentDate = $Matches[1]
            if (-not $stats.ContainsKey($currentDate)) {
                $stats[$currentDate] = [long[]](0, 0)
            }
            continue
        }

        if ($null -ne $currentDate -and $line -match '^(\d+)\s+(\d+)\s+') {
            $stats[$currentDate][0] += [long]$Matches[1]
            $stats[$currentDate][1] += [long]$Matches[2]
        }
    }

    $rows = foreach ($entry in $stats.GetEnumerator()) {
        $added = $entry.Value[0]
        $deleted = $entry.Value[1]
        [PSCustomObject]@{
            Date    = $entry.Name
            Added   = $added
            Deleted = $deleted
            Changed = $added + $deleted
            Net     = $added - $deleted
        }
    }

    $rows | Sort-Object Date -Descending | Format-Table -AutoSize
}

Assert-GitRepository
$authorArgs = @(Get-AuthorArguments)

switch ($Mode) {
    "Uncommitted" {
        Write-Host "Uncommitted changes:"
        $lines = @(& git -C $Repo diff HEAD --numstat)
        Write-LineTotal (Get-LineTotal $lines)
    }

    "Today" {
        Write-Host "Committed changes today:"
        $gitArgs = @("-C", $Repo, "log", "--since=midnight", "--no-merges") +
            $authorArgs + @("--numstat", "--format=tformat:")
        $lines = @(& git @gitArgs)
        Write-LineTotal (Get-LineTotal $lines)
    }

    "Date" {
        if ($Date -notmatch '^\d{4}-\d{2}-\d{2}$') {
            throw "-Mode Date requires -Date YYYY-MM-DD."
        }
        Write-Host "Committed changes on ${Date}:"
        $gitArgs = @("-C", $Repo, "log", "--since=$Date 00:00:00", "--until=$Date 23:59:59", "--no-merges") +
            $authorArgs + @("--numstat", "--format=tformat:")
        $lines = @(& git @gitArgs)
        Write-LineTotal (Get-LineTotal $lines)
    }

    "Days" {
        Write-Host "Committed changes in the last $Days days:"
        $gitArgs = @("-C", $Repo, "log", "--since=$Days days ago", "--no-merges") +
            $authorArgs + @("--date=short", "--numstat", "--format=@@@%ad")
        $lines = @(& git @gitArgs)
        Write-DailyStats $lines
    }

    "All" {
        Write-Host "Committed changes in all history:"
        $gitArgs = @("-C", $Repo, "log", "--no-merges") +
            $authorArgs + @("--date=short", "--numstat", "--format=@@@%ad")
        $lines = @(& git @gitArgs)
        Write-DailyStats $lines
    }
}
