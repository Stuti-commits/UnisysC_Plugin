function f(){
  const p1,  // Noncompliant {{Rename this constant 'p1' to match the regular expression ^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$}}
    p2 = 1; // Noncompliant
  var p1 = 1;
}
