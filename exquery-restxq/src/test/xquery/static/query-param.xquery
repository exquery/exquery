xquery version "3.0";

module namespace test = "http://test.com";

declare namespace rest = "http://exquery.org/ns/restxq";

(: with default params :)
declare
    %rest:GET
    %rest:path("/test")
    %rest:query-param("hello", "{$hello}", "hello", "again")
function test:hello($hello) {
    <hello>{$hello}</hello>
};

declare
    %rest:GET
    %rest:path("/test1")
    %rest:query-param("hello", "{$hello}", "")
function test:hello1($hello as xs:string+) {
    <hello>{$hello}</hello>
};

declare
    %rest:GET
    %rest:path("/test2")
    %rest:query-param("hello", "{$hello}", "")
function test:hello2($hello as xs:string*) {
    <hello>{$hello}</hello>
};

(: must  fail :)
(:
declare
    %rest:GET
    %rest:path("/test3")
    %rest:query-param("hello", "{$hello}", "")
function test:hello3($hello as xs:string?) {
    <hello>{$hello}</hello>
};
:)
(:
declare
    %rest:GET
    %rest:path("/test4")
    %rest:query-param("hello", "{$hello}", "")
function test:hello4($hello as xs:string) {
    <hello>{$hello}</hello>
};
:)
(: fail - has additional parameter which does not allow ZERO cardinality! :)
(:
declare
    %rest:GET
    %rest:path("/test5")
    %rest:query-param("hello", "{$hello}", "hello", "again")
function test:hello5($hello, $other as xs:string+) {
    <hello>{$hello}</hello>
};
:)



(: without default params :)
declare
    %rest:GET
    %rest:path("/test6")
    %rest:query-param("hello", "{$hello}")
function test:hello6($hello as xs:string*) {
    <hello>{$hello}</hello>
};

declare
    %rest:GET
    %rest:path("/test7")
    %rest:query-param("hello", "{$hello}")
function test:hello7($hello) {
    <hello>{$hello}</hello>
};

(: must fail :)
(:
declare
    %rest:GET
    %rest:path("/test8")
    %rest:query-param("hello", "{$hello}")
function test:hello8($hello as xs:string+) {
    <hello>{$hello}</hello>
};
:)
(:
declare
    %rest:GET
    %rest:path("/test9")
    %rest:query-param("hello", "{$hello}")
function test:hello9($hello as xs:string?) {
    <hello>{$hello}</hello>
};
:)
(:
declare
    %rest:GET
    %rest:path("/test10")
    %rest:query-param("hello", "{$hello}")
function test:hello10($hello as xs:string) {
    <hello>{$hello}</hello>
};
:)
(: fail - has additional parameter which does not allow ZERO cardinality! :)
(:
declare
    %rest:GET
    %rest:path("/test11")
    %rest:query-param("hello", "{$hello}")
function test:hello11($hello, $other as xs:string+) {
    <hello>{$hello}</hello>
};
:)
