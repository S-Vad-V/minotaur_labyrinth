if(A,B,C):-A,B;\+A,C.
:- use_module(library(socket)).

:- dynamic exit/1.


e(north) --> [north].
e(south) --> [south].
e(west) --> [west].
e(east) --> [east].
exits([Exit]) --> e(Exit).
exits([Exit|Exits]) --> e(Exit), exits(Exits).
parse_exits(Exits) --> [exits], exits(Exits), ['.'].

parse(Tokens) :- phrase(parse_exits(Exits), Tokens, Rest), retractall(exit(_)), assert(exit(Exits)).

parse(_).

filter([],[]).

filter([H1,H2,H3,H4|T],T2):-
    char_code(C1,H1),
    char_code(C2,H2),
    char_code(C3,H3),
    char_code(C4,H4),
    member(C1,['c']),
    member(C2,['e']),
    member(C3,['l']),
    member(C4,['l']) ->
    filter2(T,T2),!;filter([H2,H3,H4|T],T2).

filter2([H,H1|T],[H|T]):-
    char_code(C,H1),
    not(member(C,[')'])) -> !;
    filter2([H1|T],T).


/* Convert to lower case if necessary,
skips some characters,
works with non latin characters in SWI Prolog. */
filter_codes([], []).
filter_codes([H|T1], T2) :-
  char_code(C, H),
  member(C, ['(', ')', ':']),
  filter_codes(T1, T2).
filter_codes([H|T1], [F|T2]) :- 
  code_type(F, to_lower(H)),
  filter_codes(T1, T2).

list_member(X,[X|_]).
list_member(X,[_|TAIL]) :- list_member(X,TAIL).

process(Stream) :-
  exit(Exits),
  if(list_member(west, Exits),
    (format(atom(Command), 'move ~w~n', "west")),
    if(list_member(north, Exits),
        (format(atom(Command), 'move ~w~n', "north")),
        (exit([Direction,_], format(atom(Command), 'move ~w~n', [Direction])))
        )
    ),
  write(Command),
  write(Stream, Command),
  flush_output(Stream),
  retractall(exit(_)).

process(_).


startLoop(Stream) :-
  flush(),
  sleep(1),
  writeln(Stream, 'Vad Bot'),
  flush_output(Stream),
  loop(Stream).

loop(Stream) :-
  read_line_to_codes(Stream, Codes),  
  write(Codes),
  filter(Codes, Exits),  
  write(Exits),
  filter_codes(Exits, Filtered),
  write(Filtered),
  atom_codes(Atom, Filtered),
  tokenize_atom(Atom, Tokens),
  write(Tokens),
  parse(Tokens),
  nl,
  flush(),
  sleep(1),
  process(Stream),
  loop(Stream).
 
 main :-
   setup_call_cleanup(
     tcp_connect(localhost:3333, Stream, []),
     startLoop(Stream),
     close(Stream)).
     
